package dev.elelan.quotequiz.contract.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestionDto(
    val id: String,
    val quote: String,
    val mode: QuizMode,
    val progress: Int,
    val totalQuestions: Int,
    val proposedAuthor: String? = null,
    val options: List<QuizOptionDto> = emptyList(),
)
