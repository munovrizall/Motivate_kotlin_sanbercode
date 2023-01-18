package com.artonov.motivate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.artonov.motivate.api.ApiRequests
import com.artonov.motivate.api.QuoteJson
import com.artonov.motivate.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.zip.Inflater

const val BASE_URL = "https://api.forismatic.com/"
val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: QuoteViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }
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
}