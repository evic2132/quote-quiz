package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionStartRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quote_quiz_server.auth.AppUserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/quiz/sessions")
class QuizController(
    private val quizService: QuizService,
) {

    @PostMapping
    fun startSession(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @RequestBody request: QuizSessionStartRequest,
    ): QuizSessionDto =
        quizService.startSession(
            userId = principal.id,
            mode = request.mode,
        )

    @PostMapping("/{sessionId}/answers")
    fun submitAnswer(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @PathVariable sessionId: String,
        @RequestBody request: SubmitAnswerRequest,
    ): SubmitAnswerResponse =
        quizService.submitAnswer(
            userId = principal.id,
            sessionId = sessionId,
            request = request,
        )
}
