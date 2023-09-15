package com.kamra.wordwise

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.wordDao())
                    }
                }
            }
        }

         fun populateDatabase(wordDao: WordDao) {
            Log.d("AppDatabase", "Inside populateDatabase()")
            // Preload words from DataSource
            val swedishWords = DataSource.preloadedSwedishWords
            val persianWords = DataSource.preloadedPersianWords

            for (word in swedishWords) {
                val id = wordDao.insert(word)
                Log.d("AppDatabase", "Inserted Swedish word with ID: $id")
            }

            for (word in persianWords) {
                val id = wordDao.insert(word)
                Log.d("AppDatabase", "Inserted Persian word with ID: $id")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            //context.deleteDatabase("word_database") // Force delete the database
            Log.d("AppDatabase", "Inside getDatabase()")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(databaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
