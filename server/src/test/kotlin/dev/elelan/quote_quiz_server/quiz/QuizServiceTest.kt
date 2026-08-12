package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quote_quiz_server.user.UserRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class QuizServiceTest {

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var quizSessionRepository: QuizSessionRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `start session creates exactly ten unique questions`() {
        val userId = requireNotNull(userRepository.findByEmail("demo@example.com")).id

        val session = quizService.startSession(userId = userId, mode = QuizMode.BINARY)
        val storedSession = requireNotNull(quizSessionRepository.findBySessionId(session.sessionId))

        assertEquals(10, storedSession.questions.size)
        assertEquals(10, storedSession.questions.map { it.questionId }.distinct().size)
    }

    @Test
    fun `binary session stores only binary questions`() {
        val userId = requireNotNull(userRepository.findByEmail("demo@example.com")).id

        val session = quizService.startSession(userId = userId, mode = QuizMode.BINARY)
        val storedSession = requireNotNull(quizSessionRepository.findBySessionId(session.sessionId))

        assertTrue(storedSession.questions.all { it.mode == QuizMode.BINARY })
        assertTrue(storedSession.questions.all { it.proposedAuthor != null })
        assertTrue(storedSession.questions.all { it.options.isEmpty() })
    }

    @Test
    fun `multiple choice session stores exactly three options and one correct answer`() {
        val userId = requireNotNull(userRepository.findByEmail("demo@example.com")).id

        val session = quizService.startSession(userId = userId, mode = QuizMode.MULTIPLE_CHOICE)
        val storedSession = requireNotNull(quizSessionRepository.findBySessionId(session.sessionId))

        assertTrue(storedSession.questions.all { it.mode == QuizMode.MULTIPLE_CHOICE })
        assertTrue(storedSession.questions.all { it.proposedAuthor == null })
        assertTrue(storedSession.questions.all { it.options.size == 3 })
        assertTrue(storedSession.questions.all { question ->
            question.options.count { it == question.correctAuthor } == 1
        })
        assertEquals(listOf("A", "B", "C"), session.currentQuestion.options.map { it.id })
    }

    @Test
    fun `submitting same question twice replays stored result without rescoring`() {
        val userId = requireNotNull(userRepository.findByEmail("demo@example.com")).id
        val session = quizService.startSession(userId = userId, mode = QuizMode.BINARY)
        val storedSession = requireNotNull(quizSessionRepository.findBySessionId(session.sessionId))
        val firstQuestion = storedSession.questions.first()
        val answer = firstQuestion.proposedAuthor == firstQuestion.correctAuthor

        val firstResponse =
            quizService.submitAnswer(
                userId = userId,
                sessionId = session.sessionId,
                request =
                    SubmitAnswerRequest(
                        questionId = firstQuestion.questionId,
                        binaryAnswer = answer,
                    ),
            )

        assertNotNull(firstResponse)
        val replayedResponse =
            quizService.submitAnswer(
                userId = userId,
                sessionId = session.sessionId,
                request =
                    SubmitAnswerRequest(
                        questionId = firstQuestion.questionId,
                        binaryAnswer = !answer,
                    ),
            )

        assertEquals(firstResponse.questionId, replayedResponse.questionId)
        assertEquals(firstResponse.correct, replayedResponse.correct)
        assertEquals(firstResponse.correctAuthor, replayedResponse.correctAuthor)
        assertEquals(firstResponse.score, replayedResponse.score)
        assertEquals(firstResponse.nextQuestion?.id, replayedResponse.nextQuestion?.id)
    }

    @Test
    fun `submitting answers through question ten returns final result`() {
        val userId = requireNotNull(userRepository.findByEmail("demo@example.com")).id
        val session = quizService.startSession(userId = userId, mode = QuizMode.MULTIPLE_CHOICE)
        val storedSession = requireNotNull(quizSessionRepository.findBySessionId(session.sessionId))

        var finalResponse = quizService.submitAnswer(
            userId = userId,
            sessionId = session.sessionId,
            request =
                SubmitAnswerRequest(
                    questionId = storedSession.questions.first().questionId,
                    selectedOptionId = correctOptionIdFor(storedSession.questions.first()),
                ),
        )

        storedSession.questions.drop(1).forEach { question ->
            finalResponse = quizService.submitAnswer(
                userId = userId,
                sessionId = session.sessionId,
                request =
                    SubmitAnswerRequest(
                        questionId = question.questionId,
                        selectedOptionId = correctOptionIdFor(question),
                    ),
            )
        }

        assertTrue(finalResponse.completed)
        assertNotNull(finalResponse.result)
        assertEquals(10, finalResponse.result?.totalQuestions)
        assertEquals(10, finalResponse.result?.correctAnswers)
        assertEquals(0, finalResponse.result?.incorrectAnswers)
        assertEquals(100, finalResponse.result?.percentageScore)
        assertEquals(null, finalResponse.nextQuestion)
    }

    private fun correctOptionIdFor(question: QuizSessionQuestionEntity): String =
        when (question.correctAuthor) {
            question.optionOne -> "A"
            question.optionTwo -> "B"
            question.optionThree -> "C"
            else -> error("Correct author is missing from question options")
        }
}
