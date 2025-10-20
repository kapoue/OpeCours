package com.kapoue.opecours.util

import android.util.Log

object DebugUtils {
    private const val TAG = "OpeCours"
    
    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    
    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
    
    fun logDebug(message: String) {
        Log.d(TAG, message)
    }
}