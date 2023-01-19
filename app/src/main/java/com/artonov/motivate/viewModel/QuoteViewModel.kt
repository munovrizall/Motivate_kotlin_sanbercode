package com.artonov.motivate.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuoteViewModel: ViewModel() {
    var quote = "quote"
    var author = "author"

    val currentQuote: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    val currentAuthor: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}