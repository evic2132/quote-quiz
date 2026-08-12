package dev.elelan.quotequiz.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserDto,
)
