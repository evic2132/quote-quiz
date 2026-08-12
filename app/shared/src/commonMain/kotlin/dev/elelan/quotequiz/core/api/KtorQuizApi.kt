package dev.elelan.quotequiz.core.api

import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionStartRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.network.runApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorQuizApi(
    private val httpClient: HttpClient,
) : QuizApi {
    override suspend fun startSession(token: String, request: QuizSessionStartRequest): ApiResult<QuizSessionDto> =
        runApiCall {
            httpClient.post("/api/v1/quiz/sessions") {
                bearerAuth(token)
                setBody(request)
            }.body()
        }

    override suspend fun submitAnswer(
        token: String,
        sessionId: String,
        request: SubmitAnswerRequest,
    ): ApiResult<SubmitAnswerResponse> =
        runApiCall {
            httpClient.post("/api/v1/quiz/sessions/$sessionId/answers") {
                bearerAuth(token)
                setBody(request)
            }.body()
        }
}
