package com.kamra.wordwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamra.wordwise.models.Word
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {

    private lateinit var wordsAdapter: WordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Select Language"

        val selectedLanguage = intent.getStringExtra("chosenLanguage")
        val wordsList = getWordsForLanguage(selectedLanguage)

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)  // This sets how the items will be displayed

        wordsAdapter = WordsAdapter(this, wordsList) { word ->
            val intent = Intent(this, WordDetailActivity::class.java).apply {
                putExtra("word", word.term)
                putExtra("definition", word.definition)
            }
            startActivity(intent)
        }

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = wordsAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // Call this method to finish the current activity and return to the previous one
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getWordsForLanguage(language: String?): List<Word> {
        return when(language) {
            "Swedish" -> listOf(
                Word("Hej", "A Swedish greeting meaning Hello."),
                Word("Tack", "A Swedish word meaning Thank you."),
                Word("Bil", "Swedish term for Car."),
                Word("Hus", "A Swedish term for House.")
            )
            "Persian" -> listOf(
                // Replace with your Persian words
                Word("Salam", "A Persian greeting meaning Hello."),
                Word("Merci", "A Persian word meaning Thank you.")
            )
            else -> emptyList()
        }
    }
}
