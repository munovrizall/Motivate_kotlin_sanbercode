package com.artonov.motivate.database

import androidx.room.*

@Dao
interface TodoDao {
    @Insert
    fun insert(todo: Todo): Long

    @Update
    fun update(todo: Todo): Int

    @Delete
    fun delete(todo: Todo): Int

    @Query("SELECT * FROM todo_table ORDER BY dateTime DESC")
    fun get(): List<Todo>

    @Query("SELECT * FROM todo_table WHERE id = :id")
    fun getById(id: Long): Todo
}