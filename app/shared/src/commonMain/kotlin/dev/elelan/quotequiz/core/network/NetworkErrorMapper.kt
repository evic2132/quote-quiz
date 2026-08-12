package dev.elelan.quotequiz.core.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException

internal suspend fun <T> runApiCall(block: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Throwable) {
        ApiResult.Failure(exception.toApiError())
    }

internal fun Throwable.toApiError(): ApiError =
    when (this) {
        is ClientRequestException -> response.status.toApiError()
        is RedirectResponseException -> response.status.toApiError()
        is ServerResponseException -> response.status.toApiError()
        is ResponseException -> response.status.toApiError()
        is ConnectTimeoutException, is SocketTimeoutException, is UnresolvedAddressException -> ApiError.Network
        else -> ApiError.Unknown(this)
    }

internal fun HttpStatusCode.toApiError(): ApiError =
    when (value) {
        401 -> ApiError.Unauthorized
        else -> ApiError.Http(value)
    }
