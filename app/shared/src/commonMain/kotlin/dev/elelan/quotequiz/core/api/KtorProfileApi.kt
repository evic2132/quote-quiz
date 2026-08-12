package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.network.runApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

class KtorProfileApi(
    private val httpClient: HttpClient,
) : ProfileApi {
    override suspend fun getCurrentUser(token: String): ApiResult<UserDto> =
        runApiCall {
            httpClient.get("/api/v1/me") {
                bearerAuth(token)
            }.body()
        }
}
