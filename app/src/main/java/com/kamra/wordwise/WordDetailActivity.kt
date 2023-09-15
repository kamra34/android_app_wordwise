package com.kamra.wordwise

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.EditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WordDetailActivity : AppCompatActivity() {

    private lateinit var etWordDetailTerm: EditText
    private lateinit var etWordDetailDefinition: EditText
    private var isEditable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)

        etWordDetailTerm = findViewById(R.id.etWordDetailTerm)
        etWordDetailDefinition = findViewById(R.id.etWordDetailDefinition)

        val wordTerm = intent.getStringExtra("word")
        val wordDefinition = intent.getStringExtra("definition")

        etWordDetailTerm.setText(wordTerm)
        etWordDetailDefinition.setText(wordDefinition)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Word Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val editItem = menu?.add(Menu.NONE, R.id.action_edit_save, Menu.NONE, "")
        editItem?.setIcon(android.R.drawable.ic_menu_edit)
        editItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_save -> {
                if (isEditable) {
                    // Save the word to the database
                    updateWordInDatabase(etWordDetailTerm.text.toString(), etWordDetailDefinition.text.toString())
                    isEditable = false

                    etWordDetailTerm.isFocusable = false
                    etWordDetailTerm.isFocusableInTouchMode = false
                    etWordDetailTerm.isClickable = false

                    etWordDetailDefinition.isFocusable = false
                    etWordDetailDefinition.isFocusableInTouchMode = false
                    etWordDetailDefinition.isClickable = false

                    item.setIcon(android.R.drawable.ic_menu_edit) // change icon to edit icon
                } else {
                    isEditable = true

                    etWordDetailTerm.isFocusable = true
                    etWordDetailTerm.isFocusableInTouchMode = true
                    etWordDetailTerm.isClickable = true

                    etWordDetailDefinition.isFocusable = true
                    etWordDetailDefinition.isFocusableInTouchMode = true
                    etWordDetailDefinition.isClickable = true

                    item.setIcon(android.R.drawable.ic_menu_save)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateWordInDatabase(term: String, definition: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val word = db.wordDao().getWordByTerm(term)
            word?.let {
                it.term = term
                it.definition = definition
                db.wordDao().updateWord(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
