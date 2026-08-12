package dev.elelan.quotequiz.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
)
