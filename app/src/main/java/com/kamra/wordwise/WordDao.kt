package com.kamra.wordwise

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word): Long

    @Query("SELECT * FROM word_table")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT COUNT(*) FROM word_table WHERE language = :language")
    fun countWordsByLanguage(language: String): LiveData<Int>  // changed to LiveData

    @Query("SELECT * FROM word_table WHERE language = :language")
    fun getWordsByLanguage(language: String): LiveData<List<Word>>

}

