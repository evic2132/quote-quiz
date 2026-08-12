package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAnswerResponse(
    val questionId: String,
    val correct: Boolean,
    val correctAuthor: String,
    val score: Int,
    val completed: Boolean,
    val nextQuestion: QuizQuestionDto? = null,
    val result: QuizResultDto? = null,
)
