package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAnswerRequest(
    val questionId: String,
    val answer: String,
)
