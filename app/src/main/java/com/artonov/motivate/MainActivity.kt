package com.artonov.motivate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.artonov.motivate.adapter.TodoAdapter
import com.artonov.motivate.api.ApiRequests
import com.artonov.motivate.database.AppDatabase
import com.artonov.motivate.databinding.ActivityMainBinding
import com.artonov.motivate.viewModel.QuoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.forismatic.com/"
val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: QuoteViewModel
    private val todoAdapter: TodoAdapter by lazy { TodoAdapter() }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
        loadData()

        // View Model
        viewModel = ViewModelProvider(this).get(QuoteViewModel::class.java)
        viewModel.currentQuote.observe(this, Observer {
            binding.tvQuote.text = it.toString()
        })

        viewModel.currentAuthor.observe(this, Observer {
            binding.tvAuthor.text = it.toString()
        })

        getQuote()

        binding.fabAddTodo.setOnClickListener{
            navigate(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_about -> {
                val i = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(i)
                return true
            }
            else -> return true
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun getQuote() {
        binding.layoutRefresh.setOnClickListener {
            binding.tvQuote.visibility = View.INVISIBLE
            binding.tvAuthor.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            val api: ApiRequests = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequests::class.java)

            GlobalScope.launch(Dispatchers.IO) {

                try {
                    val response = api.getQuote().awaitResponse()
                    if (response.isSuccessful) {
                        val data = response.body()!!
                        Log.d(TAG, data.toString())

                        withContext(Dispatchers.Main) {
                            binding.tvQuote.visibility = View.VISIBLE
                            binding.tvAuthor.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE

                            viewModel.currentQuote.value = data.quoteText

                            if (!data.quoteAuthor.isBlank()) {
                                viewModel.currentAuthor.value = "-" + data.quoteAuthor
                            } else {
                                viewModel.currentAuthor.value = getString(R.string.blank_author)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Check your internet connection!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun navigate(id: Long) {
        val intent = Intent(this@MainActivity, AddActivity::class.java)
        intent.putExtra("ID", id)
        startActivity(intent)
    }

    private fun setAdapter() {
        todoAdapter.setOnClickListener {
            navigate(it.id)
        }
        todoAdapter.setOnDeleteListener {
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",) {
                    dialog, _ ->
                    AppDatabase.getInstance(this).todoDao().delete(it)
                    loadData()
                    Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("No") {
                    dialog, _ -> dialog.dismiss()
                }
                .show()
        }
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = todoAdapter
    }

    private fun loadData() {
        val todoList = AppDatabase.getInstance(this).todoDao().get()
        todoAdapter.updateData(todoList)
    }
}