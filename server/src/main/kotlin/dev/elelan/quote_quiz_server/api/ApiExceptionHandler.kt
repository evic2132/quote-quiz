package dev.elelan.quote_quiz_server.api

import dev.elelan.quote_quiz_server.quiz.QuizQuestionNotFoundException
import dev.elelan.quote_quiz_server.quiz.QuizSessionNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(exception.statusCode)
            .body(
                ApiErrorResponse(
                    code = exception.statusCode.toString(),
                    message = exception.reason ?: "Request failed",
                ),
            )

    @ExceptionHandler(BadCredentialsException::class, UsernameNotFoundException::class)
    fun handleInvalidCredentials(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiErrorResponse(
                    code = "INVALID_CREDENTIALS",
                    message = "Invalid credentials",
                ),
            )

    @ExceptionHandler(QuizSessionNotFoundException::class)
    fun handleQuizSessionNotFound(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiErrorResponse(
                    code = "QUIZ_SESSION_NOT_FOUND",
                    message = "Quiz session not found",
                ),
            )

    @ExceptionHandler(QuizQuestionNotFoundException::class)
    fun handleQuizQuestionNotFound(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiErrorResponse(
                    code = "QUESTION_NOT_FOUND",
                    message = "Question not found in quiz session",
                ),
            )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationFailure(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiErrorResponse(
                    code = "BAD_REQUEST",
                    message = "Invalid request body",
                ),
            )

    @ExceptionHandler(HttpMessageNotReadableException::class, IllegalArgumentException::class)
    fun handleInvalidRequest(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiErrorResponse(
                    code = "BAD_REQUEST",
                    message = "Invalid request body",
                ),
            )

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedFailure(): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiErrorResponse(
                    code = "INTERNAL_SERVER_ERROR",
                    message = "Unexpected server error",
                ),
            )
}
