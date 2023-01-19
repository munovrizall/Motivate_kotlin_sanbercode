package com.artonov.motivate

import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.artonov.motivate.database.AppDatabase
import com.artonov.motivate.database.Todo
import com.artonov.motivate.util.DateTimeUtil
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    private var id: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        id = intent.getLongExtra("ID", 0)

        if (id == 0L){
            getActionBar()?.setTitle("Insert Todo");
            getSupportActionBar()?.setTitle("Insert Todo");
        } else {
            getActionBar()?.setTitle("Update Todo");
            getSupportActionBar()?.setTitle("Update Todo");

            val todo = AppDatabase.getInstance(this).todoDao().getById(id)
            edt_todo_title.setText(todo.title)
            edt_todo_desc.setText(todo.description)
        }

        setListener()
    }

    private fun setListener() {
        fab_done.setOnClickListener() {
            val message = validate()

            if (message == "") {
                val title = edt_todo_title.text.toString()
                val description = edt_todo_desc.text.toString()

                if (id == 0L) {
                    insert(title, description)
                } else {
                    update(id, title, description)
                }
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validate(): String {
        val title = edt_todo_title.text.toString()
        val description = edt_todo_desc.text.toString()

        return if (title.isEmpty()) "Please input title"
        else if(description.isEmpty()) "Please input description"
        else ""
    }

    private fun insert(title: String, description: String) {
        val todo = Todo(0, title, description, DateTimeUtil.getCurrentDate())
        val id = AppDatabase.getInstance(this).todoDao().insert(todo)

        if (id > 0){
            Toast.makeText(this, "Insert Success", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error, Please try again", Toast.LENGTH_SHORT).show()

        }
    }

    private fun update(id: Long, title: String, description: String) {
        val todo = Todo(id, title, description, DateTimeUtil.getCurrentDate())
        val row = AppDatabase.getInstance(this).todoDao().update(todo)

        if (row > 0){
            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error, Please try again", Toast.LENGTH_SHORT).show()

        }
    }
}