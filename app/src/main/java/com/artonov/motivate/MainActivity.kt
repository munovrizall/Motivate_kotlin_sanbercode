package com.artonov.motivate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentData()

        binding.layoutRefresh.setOnClickListener{
            getCurrentData()
        }

        binding.fabAddTodo.setOnClickListener{
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCurrentData() {

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

                        binding.tvQuote.text = data.quoteText

                        if (!data.quoteAuthor.isBlank()) {
                            binding.tvAuthor.text = "-" + data.quoteAuthor
                        } else {
                            binding.tvAuthor.text = getString(R.string.blank_author)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Check your internet connection!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}