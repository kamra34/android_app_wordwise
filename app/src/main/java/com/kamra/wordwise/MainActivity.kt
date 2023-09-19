package com.kamra.wordwise

import android.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView


class MainActivity : AppCompatActivity() {

    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var chosenLanguage: String
    private var isRandomMode = false
    private lateinit var originalWordList: List<Word>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Inside onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Select Language"

        chosenLanguage = intent.getStringExtra("chosenLanguage") ?: ""

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        wordsAdapter = WordsAdapter(this,
            clickListener = { word ->
                val intent = Intent(this@MainActivity, WordDetailActivity::class.java).apply {
                    putExtra("word", word.term)
                    putExtra("definition", word.definition)
                }
                startActivity(intent)
            },
            longClickListener = { word ->
                showWordOptionsDialog(word)
                true
            })

        val randomButton: FloatingActionButton = findViewById(R.id.btn_random)
        randomButton.visibility = View.GONE
        randomButton.setOnClickListener {
            isRandomMode = true
            getWords() // This will fetch random words
            supportActionBar?.title = "Random Words"
        }

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = wordsAdapter

        // Floating Action Button
        val fab: FloatingActionButton = findViewById(R.id.fab_add_word)
        fab.setOnClickListener {
            openAddWordDialog()
        }

        getWords()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shuffle_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterWords(newText.orEmpty())
                return true
            }
        })
        return true
    }

    private fun filterWords(query: String) {
        Log.d("Filtering", "Original Words: ${originalWordList.joinToString { it.term }}")
        val filteredWords = originalWordList.filter {
            it.term.contains(query, ignoreCase = true)
        }
        Log.d("Filtering", "Filtered Words: ${filteredWords.joinToString { it.term }}")
        wordsAdapter.updateWords(filteredWords)
    }

    private fun showWordOptionsDialog(word: Word) {
        AlertDialog.Builder(this)
            .setTitle("Choose an action")
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                when (which) {
                    0 -> editWordDialog(word)
                    1 -> confirmDeleteWord(word)
                }
            }
            .show()
    }

    private fun editWordDialog(word: Word) {
        val dialog = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_word, null)
        dialog.setView(dialogView)

        val editTextWord = dialogView.findViewById<EditText>(R.id.editTextWord)
        val editTextDefinition = dialogView.findViewById<EditText>(R.id.editTextDefinition)

        editTextWord.setText(word.term)
        editTextDefinition.setText(word.definition)

        dialog.setTitle("Edit Word")
        dialog.setPositiveButton("Update") { _, _ ->
            val updatedWord = editTextWord.text.toString().trim()
            val updatedDefinition = editTextDefinition.text.toString().trim()

            if (updatedWord.isNotEmpty() && updatedDefinition.isNotEmpty()) {
                updateWordInDatabase(word, updatedWord, updatedDefinition)
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancel") { _, _ -> }

        dialog.create().show()
    }
    private fun updateWordInDatabase(oldWord: Word, newWord: String, newDefinition: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            oldWord.term = newWord
            oldWord.definition = newDefinition
            AppDatabase.getDatabase(this@MainActivity).wordDao().updateWord(oldWord)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Word updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteWord(word: Word) {
        AlertDialog.Builder(this)
            .setTitle("Delete Word")
            .setMessage("Are you sure you want to delete '${word.term}' and its definition?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWordFromDatabase(word)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteWordFromDatabase(word: Word) {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(this@MainActivity).wordDao().delete(word)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Word deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MainActivity", "Inside onOptionsItemSelected()")

        return when (item.itemId) {
            android.R.id.home -> {
                if (isRandomMode) {
                    isRandomMode = false
                    getWords() // Load all words
                    supportActionBar?.title = "Select Language" // Reset title
                    true
                } else {
                    onBackPressed()
                    true
                }
            }
            R.id.action_shuffle -> {   // Handle the shuffle action here
                isRandomMode = true
                getWords() // This will fetch random words
                supportActionBar?.title = "Random Words"
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getWords() {
        val db = AppDatabase.getDatabase(this)

        if (isRandomMode) {
            db.wordDao().getRandomWords(chosenLanguage).observe(this) { words ->
                originalWordList = words
                wordsAdapter.words = words.toMutableList()
                wordsAdapter.notifyDataSetChanged()
            }
        } else {
            if (chosenLanguage.isNotEmpty()) {
                db.wordDao().getWordsByLanguage(chosenLanguage).observe(this) { words ->
                    originalWordList = words
                    wordsAdapter.words = words.toMutableList()
                    wordsAdapter.notifyDataSetChanged()

                    // Check if there are less than 5 words
                    val randomButton: FloatingActionButton  = findViewById(R.id.btn_random)
                    if (words.size < 5) {
                        randomButton.isEnabled = false
                        randomButton.setOnClickListener {
                            Toast.makeText(this, "There are not enough words to select.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        randomButton.isEnabled = true
                        randomButton.setOnClickListener {
                            isRandomMode = true
                            getWords() // This will fetch random words
                            supportActionBar?.title = "Random Words"
                        }
                    }
                }
            } else {
                db.wordDao().getAllWords().observe(this) { words ->
                    originalWordList = words
                    wordsAdapter.words = words.toMutableList()
                    wordsAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun openAddWordDialog() {
        val dialog = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_word, null)
        dialog.setView(dialogView)

        val editTextWord = dialogView.findViewById<EditText>(R.id.editTextWord)
        val editTextDefinition = dialogView.findViewById<EditText>(R.id.editTextDefinition)

        dialog.setTitle("Add New Word")
        dialog.setPositiveButton("Add") { _, _ ->
            val word = editTextWord.text.toString().trim()
            val definition = editTextDefinition.text.toString().trim()

            if (word.isNotEmpty() && definition.isNotEmpty()) {
                saveWordToDatabase(word, definition)
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancel") { _, _ -> }

        dialog.create().show()
    }

    private fun saveWordToDatabase(word: String, definition: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val wordEntry = Word(term = word, definition = definition, language = chosenLanguage)
            AppDatabase.getDatabase(this@MainActivity).wordDao().insert(wordEntry)
        }
        Toast.makeText(this, "Word added successfully", Toast.LENGTH_SHORT).show()
    }
}
