package dev.elelan.quotequiz.core.network

actual fun Throwable.isPlatformNetworkError(): Boolean = false
