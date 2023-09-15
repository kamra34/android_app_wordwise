package com.kamra.wordwise

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word): Long

    @Update
    fun updateWord(word: Word)

    @Query("SELECT * FROM word_table WHERE term = :term LIMIT 1")
    fun getWordByTerm(term: String): Word?

    @Query("SELECT * FROM word_table")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT COUNT(*) FROM word_table WHERE language = :language")
    fun countWordsByLanguage(language: String): LiveData<Int>

    @Query("SELECT * FROM word_table WHERE language = :language")
    fun getWordsByLanguage(language: String): LiveData<List<Word>>

    @Delete
    fun delete(word: Word)

    @Query("SELECT * FROM word_table WHERE language = :language ORDER BY RANDOM() LIMIT 5")
    fun getRandomWords(language: String): LiveData<List<Word>>


}
