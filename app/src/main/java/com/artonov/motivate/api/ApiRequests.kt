package com.artonov.motivate.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiRequests {

    @GET("/api/1.0/?method=getQuote&lang=en&format=json")
    fun getQuote(): Call<QuoteJson>
}