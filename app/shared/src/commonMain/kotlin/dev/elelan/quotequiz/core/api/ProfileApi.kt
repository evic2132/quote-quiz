package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.network.ApiResult

interface ProfileApi {
    suspend fun getCurrentUser(token: String): ApiResult<UserDto>
}
