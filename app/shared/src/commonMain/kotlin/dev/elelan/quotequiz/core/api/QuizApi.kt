package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionStartRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quotequiz.core.network.ApiResult

interface QuizApi {
    suspend fun startSession(token: String, request: QuizSessionStartRequest): ApiResult<QuizSessionDto>

    suspend fun submitAnswer(
        token: String,
        sessionId: String,
        request: SubmitAnswerRequest,
    ): ApiResult<SubmitAnswerResponse>
}
