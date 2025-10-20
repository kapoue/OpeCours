package com.kapoue.opecours.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun isMarketOpen(): Boolean {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        
        return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY &&
               (hour > Constants.MARKET_OPEN_HOUR || 
                (hour == Constants.MARKET_OPEN_HOUR && minute >= Constants.MARKET_OPEN_MINUTE)) &&
               (hour < Constants.MARKET_CLOSE_HOUR || 
                (hour == Constants.MARKET_CLOSE_HOUR && minute <= Constants.MARKET_CLOSE_MINUTE))
    }
}