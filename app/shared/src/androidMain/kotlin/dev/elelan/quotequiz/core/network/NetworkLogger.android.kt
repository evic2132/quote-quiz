package dev.elelan.quotequiz.core.network

import android.util.Log

actual fun logNetworkMessage(message: String) {
    Log.d("QuoteQuizHttp", message)
}
