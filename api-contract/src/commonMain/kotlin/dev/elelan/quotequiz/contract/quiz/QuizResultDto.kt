package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizResultDto(
    val mode: QuizMode,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val percentageScore: Int,
)
