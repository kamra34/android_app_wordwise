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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var chosenLanguage: String

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
                confirmDeleteWord(word)
                true
            })

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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getWords() {
        Log.d("MainActivity", "Inside getWords()")
        val db = AppDatabase.getDatabase(this)
        Log.d("MainActivity", "After getDB()")

        if (chosenLanguage.isNotEmpty()) {
            db.wordDao().getWordsByLanguage(chosenLanguage).observe(this) { words ->
                wordsAdapter.words = words.toMutableList()
                wordsAdapter.notifyDataSetChanged()
            }
        } else {
            db.wordDao().getAllWords().observe(this) { words ->
                wordsAdapter.words = words.toMutableList()
                wordsAdapter.notifyDataSetChanged()
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
