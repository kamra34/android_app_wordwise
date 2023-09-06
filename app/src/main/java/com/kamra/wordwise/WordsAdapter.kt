package com.kamra.wordwise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kamra.wordwise.models.Word

class WordsAdapter(private val context: Context,
                   private val words: List<Word>,
                   private val clickListener: (Word) -> Unit)
    : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    // This class holds references to the views of item_word.xml
    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
    }

    // Inflates the item layout, and creates a WordViewHolder with this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    // Binds the data to the TextView for each item
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWord.text = word.term

        // Set the onClick listener for the word
        holder.itemView.setOnClickListener {
            clickListener(word)
        }
    }

    // Returns the size of the words list
    override fun getItemCount(): Int {
        return words.size
    }
}
