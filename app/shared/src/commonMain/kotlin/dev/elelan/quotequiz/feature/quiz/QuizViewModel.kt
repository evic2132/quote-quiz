package dev.elelan.quotequiz.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.settings.QuizPreferencesRepository
import dev.elelan.quotequiz.ui.core.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.quiz_error_http
import quotequiz.app.shared.generated.resources.quiz_error_network
import quotequiz.app.shared.generated.resources.quiz_error_unauthorized
import quotequiz.app.shared.generated.resources.quiz_error_unknown

class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val quizPreferencesRepository: QuizPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<QuizUiState>
        field = MutableStateFlow(QuizUiState())

    private var loadSessionJob: Job? = null

    init {
        observeSelectedMode()
    }

    fun onAction(action: QuizAction) {
        when (action) {
            QuizAction.RetryClicked -> loadSession()
            QuizAction.FeedbackConfirmed -> confirmFeedback()
            QuizAction.RestartQuizClicked -> restartQuiz()
            is QuizAction.SubmitBinaryAnswer -> submitBinaryAnswer(action.answer)
            is QuizAction.SubmitMultipleChoiceAnswer -> submitMultipleChoiceAnswer(action.optionId)
        }
    }

    private fun observeSelectedMode() {
        viewModelScope.launch {
            quizPreferencesRepository.selectedMode.collectLatest { mode ->
                uiState.update { it.copy(mode = mode) }
                loadSession(mode)
            }
        }
    }

    private fun loadSession(mode: QuizMode = uiState.value.mode) {
        loadSessionJob?.cancel()
        uiState.update {
            it.copy(
                mode = mode,
                isLoading = true,
                isSubmitting = false,
                error = null,
                result = null,
                feedbackDialog = null,
            )
        }

        loadSessionJob =
            viewModelScope.launch {
                when (val result = quizRepository.startSession()) {
                    is ApiResult.Success -> {
                        uiState.update {
                            it.copy(
                                sessionId = result.value.sessionId,
                                currentQuestion = result.value.currentQuestion,
                                mode = result.value.mode,
                                isLoading = false,
                                error = null,
                            )
                        }
                    }

                    is ApiResult.Failure -> {
                        uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.error.toQuizErrorText(),
                            )
                        }
                    }
                }
            }
    }

    private fun submitBinaryAnswer(answer: Boolean) {
        submitAnswer { questionId ->
            SubmitAnswerRequest(
                questionId = questionId,
                binaryAnswer = answer,
            )
        }
    }

    private fun submitMultipleChoiceAnswer(optionId: String) {
        submitAnswer { questionId ->
            SubmitAnswerRequest(
                questionId = questionId,
                selectedOptionId = optionId,
            )
        }
    }

    private fun submitAnswer(
        requestFactory: (String) -> SubmitAnswerRequest,
    ) {
        val currentState = uiState.value
        if (currentState.isLoading || currentState.isSubmitting || currentState.feedbackDialog != null) return

        val sessionId = currentState.sessionId ?: return
        val questionId = currentState.currentQuestion?.id ?: return

        uiState.update { it.copy(isSubmitting = true, error = null) }

        viewModelScope.launch {
            when (val result = quizRepository.submitAnswer(sessionId, requestFactory(questionId))) {
                is ApiResult.Success -> {
                    uiState.update {
                        it.copy(
                            isSubmitting = false,
                            feedbackDialog = QuizFeedbackDialogState(
                                correct = result.value.correct,
                                correctAuthor = result.value.correctAuthor,
                                nextQuestion = result.value.nextQuestion,
                                result = result.value.result,
                            ),
                        )
                    }
                }

                is ApiResult.Failure -> {
                    uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = result.error.toQuizErrorText(),
                        )
                    }
                }
            }
        }
    }

    private fun confirmFeedback() {
        val feedbackDialog = uiState.value.feedbackDialog ?: return
        uiState.update {
            it.copy(
                currentQuestion = feedbackDialog.nextQuestion,
                result = feedbackDialog.result,
                feedbackDialog = null,
            )
        }
    }

    private fun restartQuiz() {
        loadSession()
    }
}

private fun ApiError.toQuizErrorText(): UiText =
    when (this) {
        ApiError.Network -> UiText.StringResourceId(Res.string.quiz_error_network)
        ApiError.Unauthorized -> UiText.StringResourceId(Res.string.quiz_error_unauthorized)
        is ApiError.Http -> UiText.StringResourceId(Res.string.quiz_error_http)
        is ApiError.Unknown -> UiText.StringResourceId(Res.string.quiz_error_unknown)
    }
