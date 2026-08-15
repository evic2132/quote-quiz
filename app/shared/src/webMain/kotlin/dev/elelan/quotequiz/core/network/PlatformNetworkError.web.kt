package dev.elelan.quotequiz.core.network

import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException

actual fun Throwable.isPlatformNetworkError(): Boolean {
    // 1. Ktor-level timeouts are true network failures
    if (this is SocketTimeoutException || this is HttpRequestTimeoutException) {
        return true
    }

    // 2. Non-2xx HTTP responses (404, 500, etc.) are server responses, NOT network connection errors
    if (this is ResponseException) {
        return false
    }

    // 3. Browser-level fetch failures (Offline, CORS, Failed to Fetch, Connection Refused)
    val message = message?.lowercase().orEmpty()
    return message.contains("fetch") ||
            message.contains("networkerror") ||
            message.contains("failed to fetch") ||
            message.contains("typeerror") ||
            this::class.simpleName == "JsException"
}