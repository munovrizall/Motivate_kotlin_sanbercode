package com.artonov.motivate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        private var instance: AppDatabase? = null
        private val sLock = Object()

        fun getInstance(context: Context): AppDatabase {
            synchronized(sLock) {

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "todo_table.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                return instance as AppDatabase
            }
        }
    }
}