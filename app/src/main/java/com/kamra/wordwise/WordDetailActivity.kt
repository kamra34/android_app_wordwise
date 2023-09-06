package com.kamra.wordwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

class WordDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)

        // Retrieve the TextView from the layout
        val tvWordDetail: TextView = findViewById(R.id.tvWordDetail)

        // Retrieve the word details from the intent
        val wordDetails = intent.getStringExtra("wordDetails")

        val wordTerm = intent.getStringExtra("word")
        val wordDefinition = intent.getStringExtra("definition")

        // Set the word details to the TextView
        tvWordDetail.text = "$wordTerm: $wordDefinition"

        // initialize and set the back custom toolbar, as the support action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Words List"

        // Enable the Up (Back) button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // This function will finish the activity when the back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
