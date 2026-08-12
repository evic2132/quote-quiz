package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.network.runApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorAuthApi(
    private val httpClient: HttpClient,
) : AuthApi {
    override suspend fun login(request: LoginRequest): ApiResult<LoginResponse> =
        runApiCall {
            httpClient.post("/api/v1/auth/login") {
                setBody(request)
            }.body()
        }
}
