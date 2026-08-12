package dev.elelan.quote_quiz_server.quiz

import org.springframework.data.jpa.repository.JpaRepository

interface QuizSessionRepository : JpaRepository<QuizSessionEntity, Long> {
    fun findBySessionId(sessionId: String): QuizSessionEntity?
    fun findBySessionIdAndUserId(sessionId: String, userId: Long): QuizSessionEntity?
}
