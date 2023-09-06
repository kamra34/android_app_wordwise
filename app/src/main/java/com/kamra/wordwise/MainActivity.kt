package com.kamra.wordwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamra.wordwise.models.Word


class MainActivity : AppCompatActivity() {

    private lateinit var wordsAdapter: WordsAdapter
    private val wordsList = listOf(
        Word("Hej", "A Swedish greeting meaning Hello."),
        Word("Tack", "A Swedish word meaning Thank you."),
        Word("Bil", "Swedish term for Car."),
        Word("Hus", "A Swedish term for House.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


        recyclerView.adapter = wordsAdapter
    }
}
