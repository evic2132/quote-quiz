package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizSessionStartRequest(
    val mode: QuizMode,
)
