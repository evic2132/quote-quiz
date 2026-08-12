package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizOptionDto(
    val id: String,
    val label: String,
)
