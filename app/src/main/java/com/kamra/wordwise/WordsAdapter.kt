package com.kamra.wordwise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordsAdapter(
    private val context: Context,
    private val clickListener: (Word) -> Unit,
    private val longClickListener: (Word) -> Boolean) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    var words: MutableList<Word> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener(words[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener(words[position])
                }
                true
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWord.text = word.term
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun updateWords(newWords: List<Word>) {
        words.clear()
        words.addAll(newWords)
        notifyDataSetChanged()
    }

}
