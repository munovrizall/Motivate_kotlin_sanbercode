package com.artonov.motivate.api

data class QuoteJson(
    val quoteAuthor: String,
    val quoteLink: String,
    val quoteText: String,
    val senderLink: String,
    val senderName: String
)