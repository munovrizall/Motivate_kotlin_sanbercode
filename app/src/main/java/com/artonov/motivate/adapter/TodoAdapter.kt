package com.artonov.motivate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artonov.motivate.R
import com.artonov.motivate.database.Todo
import com.artonov.motivate.util.DateTimeUtil
import kotlinx.android.synthetic.main.list_todo_item.view.*

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var listener: ((Todo) -> Unit)? = null
    private var deleteListener: ((Todo) -> Unit)? = null
    private val todoList = arrayListOf<Todo>()

    inner class TodoViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(todo: Todo) {
            view.apply {
                tv_todo_title.text = todo.title
                tv_todo_desc.text = todo.description
                tv_datetime.text = "Last Updated " + DateTimeUtil.convertDatetime(todo.dateTime)
            }

            view.rootView.setOnClickListener {
                listener?.invoke(todo)
            }
            view.ic_delete.setOnClickListener {
                deleteListener?.invoke(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
        TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_todo_item, parent, false))


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int = todoList.size

    fun updateData(newList: List<Todo>) {
        todoList.clear()
        todoList.addAll(newList)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (Todo) -> Unit) {
        this.listener = listener
    }

    fun setOnDeleteListener(deleteListener: (Todo) -> Unit) {
        this.deleteListener = deleteListener
    }
}