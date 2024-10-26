package com.meditrust.findadoctor.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object  DateUtils {
    /* Utility classes and functions (e.g., NetworkUtils, DateUtils).*/
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(Date())
    }
}