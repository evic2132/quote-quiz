package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizSessionDto(
    val sessionId: String,
    val mode: QuizMode,
    val totalQuestions: Int,
    val currentQuestion: QuizQuestionDto,
)
