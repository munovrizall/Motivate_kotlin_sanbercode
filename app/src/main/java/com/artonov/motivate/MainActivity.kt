package com.artonov.motivate

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
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.zip.Inflater

const val BASE_URL = "https://api.forismatic.com/"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        getCurrentData()

        binding.layoutGenerateQuote.setOnClickListener{
//            getCurrentData()
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
                val response: Response<QuoteJson> = api.getQuote().awaitResponse()
                if (response.isSuccessful) {
                    val data: QuoteJson = response.body()!!

                    withContext(Dispatchers.Main) {
                        binding.tvQuote.visibility = View.VISIBLE
                        binding.tvAuthor.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE

                        binding.tvQuote.text = data.quoteText
                        binding.tvAuthor.text = data.quoteAuthor
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