package com.artonov.motivate.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun getCurrentDate(): String {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    fun convertDatetime(raw: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        val date = input.parse(raw) ?: Date()
        return output.format(date)
    }
}