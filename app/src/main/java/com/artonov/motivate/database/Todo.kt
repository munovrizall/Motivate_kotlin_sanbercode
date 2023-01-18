package com.artonov.motivate.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var title: String,
    var description: String,
    var dateTime: String
)