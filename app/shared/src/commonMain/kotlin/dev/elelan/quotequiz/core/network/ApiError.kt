package dev.elelan.quotequiz.core.network

sealed interface ApiError {
    data object Network : ApiError

    data object Unauthorized : ApiError

    data class Http(val statusCode: Int) : ApiError

    data class Unknown(val cause: Throwable) : ApiError
}
