package com.kamra.wordwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.util.Log
import androidx.lifecycle.lifecycleScope
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
        recyclerView.layoutManager =
            LinearLayoutManager(this)  // This sets how the items will be displayed

        wordsAdapter = WordsAdapter(this) { word ->
            val intent = Intent(this@MainActivity, WordDetailActivity::class.java).apply {
                putExtra("word", word.term)
                putExtra("definition", word.definition)
            }
            startActivity(intent)
        }

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = wordsAdapter

        getWords()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MainActivity", "Inside onOptionsItemSelected()")

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // Call this method to finish the current activity and return to the previous one
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
            // Default behavior when no language is specified
            db.wordDao().getAllWords().observe(this) { words ->
                wordsAdapter.words = words.toMutableList()
                wordsAdapter.notifyDataSetChanged()
            }
        }
    }
}
