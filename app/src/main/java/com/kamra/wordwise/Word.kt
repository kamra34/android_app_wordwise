package com.kamra.wordwise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // default value added
    val term: String,
    val definition: String,
    val language: String
)
