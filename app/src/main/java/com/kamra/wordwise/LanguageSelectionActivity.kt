package com.kamra.wordwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class LanguageSelectionActivity : AppCompatActivity() {
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        // Swedish flag click event
        val imgSwedishFlag = findViewById<ImageView>(R.id.imgSwedishFlag)
        imgSwedishFlag.setOnClickListener {
            openMainActivity("Swedish")
        }

        // Persian flag click event
        val imgPersianFlag = findViewById<ImageView>(R.id.imgPersianFlag)
        imgPersianFlag.setOnClickListener {
            openMainActivity("Persian")
        }

        // ... Repeat for other flags
    }

    private fun openMainActivity(language: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("chosenLanguage", language)
        }
        startActivity(intent)
    }
}
