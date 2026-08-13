package dev.elelan.quotequiz.feature.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizQuestionDto
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.core.ui.UiText

data class QuizUiState(
    val mode: QuizMode = QuizMode.BINARY,
    val sessionId: String? = null,
    val currentQuestion: QuizQuestionDto? = null,
    val result: QuizResultDto? = null,
    val error: UiText? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val feedbackDialog: QuizFeedbackDialogState? = null,
)

sealed interface QuizAction {
    data object RetryClicked : QuizAction
    data object FeedbackConfirmed : QuizAction
    data object RestartQuizClicked : QuizAction
    data class SubmitBinaryAnswer(val answer: Boolean) : QuizAction
    data class SubmitMultipleChoiceAnswer(val optionId: String) : QuizAction
}

data class QuizFeedbackDialogState(
    val correct: Boolean,
    val correctAuthor: String,
    val nextQuestion: QuizQuestionDto?,
    val result: QuizResultDto?,
)
