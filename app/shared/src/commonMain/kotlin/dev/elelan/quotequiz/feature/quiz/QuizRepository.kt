package dev.elelan.quotequiz.feature.quiz

import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionStartRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quotequiz.core.api.QuizApi
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.settings.QuizPreferencesRepository
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.session.SessionState

interface QuizRepository {
    suspend fun startSession(): ApiResult<QuizSessionDto>

    suspend fun submitAnswer(
        sessionId: String,
        request: SubmitAnswerRequest,
    ): ApiResult<SubmitAnswerResponse>
}

class DefaultQuizRepository(
    private val quizApi: QuizApi,
    private val sessionRepository: SessionRepository,
    private val quizPreferencesRepository: QuizPreferencesRepository,
) : QuizRepository {
    override suspend fun startSession(): ApiResult<QuizSessionDto> {
        val token = currentToken() ?: return ApiResult.Failure(ApiError.Unauthorized)
        return quizApi.startSession(
            token = token,
            request = QuizSessionStartRequest(mode = quizPreferencesRepository.selectedMode.value),
        )
    }

    override suspend fun submitAnswer(
        sessionId: String,
        request: SubmitAnswerRequest,
    ): ApiResult<SubmitAnswerResponse> {
        val token = currentToken() ?: return ApiResult.Failure(ApiError.Unauthorized)
        return quizApi.submitAnswer(
            token = token,
            sessionId = sessionId,
            request = request,
        )
    }

    private fun currentToken(): String? =
        (sessionRepository.sessionState.value as? SessionState.Authenticated)?.token
}
