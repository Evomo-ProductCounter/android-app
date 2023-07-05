package com.evomo.productcounterapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDateNoTime(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}