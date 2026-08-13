package dev.elelan.quotequiz.feature.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizQuestionDto
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.settings.QuizPreferencesRepository
import dev.elelan.quotequiz.core.ui.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.quiz_error_network
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {
    @Test
    fun `initial load starts binary session`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = FakeQuizRepository()
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        assertEquals(QuizMode.BINARY, repository.lastStartedMode)
        assertEquals("session-1", viewModel.uiState.value.sessionId)
        assertEquals(1, viewModel.uiState.value.currentQuestion?.progress)
        assertFalse(viewModel.uiState.value.isLoading)
        Dispatchers.resetMain()
    }

    @Test
    fun `load failure shows retryable error and retry starts session again`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = FakeQuizRepository(
            startResults = ArrayDeque(
                listOf(
                    ApiResult.Failure(ApiError.Network),
                    ApiResult.Success(binarySession()),
                ),
            ),
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        assertEquals(UiText.StringResourceId(Res.string.quiz_error_network), viewModel.uiState.value.error)

        viewModel.onAction(QuizAction.RetryClicked)
        advanceUntilIdle()

        assertEquals(2, repository.startCallCount)
        assertNull(viewModel.uiState.value.error)
        assertEquals("session-1", viewModel.uiState.value.sessionId)
        Dispatchers.resetMain()
    }

    @Test
    fun `duplicate submit does not issue duplicate answer request`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = FakeQuizRepository(
            submitResultProvider = {
                delay(50)
                ApiResult.Success(
                    SubmitAnswerResponse(
                        questionId = "q1",
                        correct = true,
                        correctAuthor = "Socrates",
                        score = 1,
                        completed = false,
                        nextQuestion = binaryQuestion(id = "q2", progress = 2),
                    ),
                )
            },
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(QuizAction.SubmitBinaryAnswer(true))
        viewModel.onAction(QuizAction.SubmitBinaryAnswer(true))
        advanceUntilIdle()

        assertEquals(1, repository.submitCallCount)
        assertEquals("q1", viewModel.uiState.value.currentQuestion?.id)
        assertNotNull(viewModel.uiState.value.feedbackDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
        Dispatchers.resetMain()
    }

    @Test
    fun `submit success stores result only after feedback confirmation`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val expectedResult = QuizResultDto(
            mode = QuizMode.BINARY,
            totalQuestions = 10,
            correctAnswers = 7,
            incorrectAnswers = 3,
            percentageScore = 70,
        )
        val repository = FakeQuizRepository(
            submitResults = ArrayDeque(
                listOf(
                    ApiResult.Success(
                        SubmitAnswerResponse(
                            questionId = "q1",
                            correct = true,
                            correctAuthor = "Socrates",
                            score = 7,
                            completed = true,
                            result = expectedResult,
                        ),
                    ),
                ),
            ),
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(QuizAction.SubmitBinaryAnswer(true))
        advanceUntilIdle()

        assertEquals("q1", viewModel.uiState.value.currentQuestion?.id)
        assertEquals(expectedResult, viewModel.uiState.value.feedbackDialog?.result)
        assertNull(viewModel.uiState.value.result)

        viewModel.onAction(QuizAction.FeedbackConfirmed)

        assertEquals(expectedResult, viewModel.uiState.value.result)
        assertNull(viewModel.uiState.value.currentQuestion)
        Dispatchers.resetMain()
    }

    @Test
    fun `answer response does not advance question before feedback confirmed`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = FakeQuizRepository(
            submitResults = ArrayDeque(
                listOf(
                    ApiResult.Success(
                        SubmitAnswerResponse(
                            questionId = "q1",
                            correct = true,
                            correctAuthor = "Socrates",
                            score = 1,
                            completed = false,
                            nextQuestion = binaryQuestion(id = "q2", progress = 2),
                        ),
                    ),
                ),
            ),
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(QuizAction.SubmitBinaryAnswer(true))
        advanceUntilIdle()

        assertEquals("q1", viewModel.uiState.value.currentQuestion?.id)
        assertEquals("q2", viewModel.uiState.value.feedbackDialog?.nextQuestion?.id)

        viewModel.onAction(QuizAction.FeedbackConfirmed)

        assertEquals("q2", viewModel.uiState.value.currentQuestion?.id)
        assertNull(viewModel.uiState.value.feedbackDialog)
        Dispatchers.resetMain()
    }

    @Test
    fun `restart quiz clears result and starts a fresh session`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val expectedResult = QuizResultDto(
            mode = QuizMode.BINARY,
            totalQuestions = 10,
            correctAnswers = 7,
            incorrectAnswers = 3,
            percentageScore = 70,
        )
        val repository = FakeQuizRepository(
            startResults = ArrayDeque(
                listOf(
                    ApiResult.Success(binarySession()),
                    ApiResult.Success(
                        binarySession().copy(
                            sessionId = "session-2",
                            currentQuestion = binaryQuestion(id = "q1-restart", progress = 1),
                        ),
                    ),
                ),
            ),
            submitResults = ArrayDeque(
                listOf(
                    ApiResult.Success(
                        SubmitAnswerResponse(
                            questionId = "q1",
                            correct = true,
                            correctAuthor = "Socrates",
                            score = 7,
                            completed = true,
                            result = expectedResult,
                        ),
                    ),
                ),
            ),
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(QuizAction.SubmitBinaryAnswer(true))
        advanceUntilIdle()
        viewModel.onAction(QuizAction.FeedbackConfirmed)

        assertEquals(expectedResult, viewModel.uiState.value.result)

        viewModel.onAction(QuizAction.RestartQuizClicked)
        advanceUntilIdle()

        assertEquals(2, repository.startCallCount)
        assertNull(viewModel.uiState.value.result)
        assertEquals("session-2", viewModel.uiState.value.sessionId)
        assertEquals("q1-restart", viewModel.uiState.value.currentQuestion?.id)
        Dispatchers.resetMain()
    }

    @Test
    fun `mode change starts a fresh session in the selected mode`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repository = FakeQuizRepository(
            startResults = ArrayDeque(
                listOf(
                    ApiResult.Success(binarySession()),
                    ApiResult.Success(
                        multipleChoiceSession(
                            sessionId = "session-2",
                            questionId = "mcq-1",
                        ),
                    ),
                ),
            ),
        )
        val preferencesRepository = FakeQuizPreferencesRepository()

        val viewModel = QuizViewModel(repository, preferencesRepository)
        advanceUntilIdle()

        preferencesRepository.updateSelectedMode(QuizMode.MULTIPLE_CHOICE)
        advanceUntilIdle()

        assertEquals(2, repository.startCallCount)
        assertEquals(QuizMode.MULTIPLE_CHOICE, repository.lastStartedMode)
        assertEquals(QuizMode.MULTIPLE_CHOICE, viewModel.uiState.value.mode)
        assertEquals("session-2", viewModel.uiState.value.sessionId)
        assertEquals("mcq-1", viewModel.uiState.value.currentQuestion?.id)
        Dispatchers.resetMain()
    }

    private class FakeQuizRepository(
        startResults: ArrayDeque<ApiResult<QuizSessionDto>> = ArrayDeque(listOf(ApiResult.Success(binarySession()))),
        submitResults: ArrayDeque<ApiResult<SubmitAnswerResponse>> = ArrayDeque(),
        private val submitResultProvider: (suspend () -> ApiResult<SubmitAnswerResponse>)? = null,
    ) : QuizRepository {
        private val queuedStartResults = startResults
        private val queuedSubmitResults = submitResults

        var lastStartedMode: QuizMode? = null
            private set

        var startCallCount: Int = 0
            private set

        var submitCallCount: Int = 0
            private set

        override suspend fun startSession(): ApiResult<QuizSessionDto> {
            lastStartedMode = queuedStartResults.firstOrNull()?.let {
                when (it) {
                    is ApiResult.Success -> it.value.mode
                    is ApiResult.Failure -> lastStartedMode
                }
            }
            startCallCount += 1
            return queuedStartResults.removeFirst()
        }

        override suspend fun submitAnswer(
            sessionId: String,
            request: SubmitAnswerRequest,
        ): ApiResult<SubmitAnswerResponse> {
            submitCallCount += 1
            return submitResultProvider?.invoke()
                ?: queuedSubmitResults.removeFirstOrNull()
                ?: ApiResult.Failure(ApiError.Unknown(IllegalStateException("No submit result configured")))
        }
    }

    private class FakeQuizPreferencesRepository(
        initialMode: QuizMode = QuizMode.BINARY,
    ) : QuizPreferencesRepository {
        private val mutableSelectedMode = MutableStateFlow(initialMode)

        override val selectedMode: StateFlow<QuizMode> = mutableSelectedMode

        override suspend fun updateSelectedMode(mode: QuizMode) {
            mutableSelectedMode.value = mode
        }
    }
}

private fun binarySession() = QuizSessionDto(
    sessionId = "session-1",
    mode = QuizMode.BINARY,
    totalQuestions = 10,
    currentQuestion = binaryQuestion(),
)

private fun binaryQuestion(
    id: String = "q1",
    progress: Int = 1,
) = QuizQuestionDto(
    id = id,
    quote = "The unexamined life is not worth living.",
    mode = QuizMode.BINARY,
    progress = progress,
    totalQuestions = 10,
    proposedAuthor = "Socrates",
)

private fun multipleChoiceSession(
    sessionId: String,
    questionId: String,
) = QuizSessionDto(
    sessionId = sessionId,
    mode = QuizMode.MULTIPLE_CHOICE,
    totalQuestions = 10,
    currentQuestion = QuizQuestionDto(
        id = questionId,
        quote = "We accept the love we think we deserve.",
        mode = QuizMode.MULTIPLE_CHOICE,
        progress = 1,
        totalQuestions = 10,
        options = listOf(),
    ),
)
