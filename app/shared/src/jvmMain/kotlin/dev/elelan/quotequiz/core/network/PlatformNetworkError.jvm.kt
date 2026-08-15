package dev.elelan.quotequiz.core.network

import java.io.IOException

actual fun Throwable.isPlatformNetworkError(): Boolean = this is IOException
