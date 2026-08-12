package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.core.network.ApiResult

interface AuthApi {
    suspend fun login(request: LoginRequest): ApiResult<LoginResponse>
}
