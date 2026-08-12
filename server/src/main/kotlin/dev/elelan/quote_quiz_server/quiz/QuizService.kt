package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizOptionDto
import dev.elelan.quotequiz.contract.quiz.QuizQuestionDto
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import dev.elelan.quote_quiz_server.quote.QuoteEntity
import dev.elelan.quote_quiz_server.quote.QuoteRepository
import dev.elelan.quote_quiz_server.user.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
    private val quizSessionRepository: QuizSessionRepository,
    private val quoteRepository: QuoteRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun startSession(userId: Long, mode: QuizMode): QuizSessionDto {
        require(userRepository.existsById(userId)) { "User not found: $userId" }

        val quotes = quoteRepository.findAll().shuffled().take(TOTAL_QUESTIONS)
        require(quotes.size == TOTAL_QUESTIONS) { "Not enough quotes to create a session." }

        val session = QuizSessionEntity(sessionId = UUID.randomUUID().toString(), userId = userId, mode = mode)
        val questions =
            quotes.mapIndexed { index, quote ->
                when (mode) {
                    QuizMode.BINARY -> createBinaryQuestion(session, quote, index + 1)
                    QuizMode.MULTIPLE_CHOICE -> createMultipleChoiceQuestion(session, quote, quotes, index + 1)
                }
            }

        session.questions.addAll(questions)
        val savedSession = quizSessionRepository.save(session)
        return QuizSessionDto(
            sessionId = savedSession.sessionId,
            mode = savedSession.mode,
            totalQuestions = TOTAL_QUESTIONS,
            currentQuestion = savedSession.questions.first().toDto(),
        )
    }

    @Transactional
    fun submitAnswer(
        userId: Long,
        sessionId: String,
        request: SubmitAnswerRequest,
    ): SubmitAnswerResponse {
        val session =
            quizSessionRepository.findBySessionIdAndUserId(sessionId, userId)
                ?: throw QuizSessionNotFoundException()
        val question =
            session.questions.find { it.questionId == request.questionId }
                ?: throw QuizQuestionNotFoundException()

        if (question.answered) {
            return buildAnswerResponse(
                session = session,
                question = question,
                correct = requireNotNull(question.wasCorrect) { "Answered question is missing stored correctness." },
            )
        }

        val correct =
            when (question.mode) {
                QuizMode.BINARY -> isBinaryAnswerCorrect(question, request.binaryAnswer)
                QuizMode.MULTIPLE_CHOICE -> isMultipleChoiceAnswerCorrect(question, request.selectedOptionId)
            }

        question.answered = true
        question.wasCorrect = correct
        if (correct) {
            session.correctAnswers += 1
        }

        session.completed = session.questions.all { it.answered }
        quizSessionRepository.save(session)
        return buildAnswerResponse(
            session = session,
            question = question,
            correct = correct,
        )
    }

    private fun buildAnswerResponse(
        session: QuizSessionEntity,
        question: QuizSessionQuestionEntity,
        correct: Boolean,
    ): SubmitAnswerResponse {
        val nextQuestion = session.questions.firstOrNull { !it.answered }
        val completed = nextQuestion == null

        return SubmitAnswerResponse(
            questionId = question.questionId,
            correct = correct,
            correctAuthor = question.correctAuthor,
            score = session.correctAnswers,
            completed = completed,
            nextQuestion = nextQuestion?.toDto(),
            result = if (completed) session.toResultDto() else null,
        )
    }

    private fun createBinaryQuestion(
        session: QuizSessionEntity,
        quote: QuoteEntity,
        progress: Int,
    ): QuizSessionQuestionEntity {
        val alternateAuthor =
            quoteRepository.findAll()
                .map { it.author }
                .distinct()
                .first { it != quote.author }
        val useCorrectAuthor = progress % 2 == 0
        return QuizSessionQuestionEntity(
            questionId = UUID.randomUUID().toString(),
            session = session,
            mode = QuizMode.BINARY,
            quoteText = quote.text,
            correctAuthor = quote.author,
            progress = progress,
            proposedAuthor = if (useCorrectAuthor) quote.author else alternateAuthor,
        )
    }

    private fun createMultipleChoiceQuestion(
        session: QuizSessionEntity,
        quote: QuoteEntity,
        quotes: List<QuoteEntity>,
        progress: Int,
    ): QuizSessionQuestionEntity {
        val distractors =
            quotes.asSequence()
                .map { it.author }
                .filter { it != quote.author }
                .distinct()
                .take(2)
                .toList()
        require(distractors.size == 2) { "Not enough distinct authors for multiple choice." }
        val options = (distractors + quote.author).shuffled()
        return QuizSessionQuestionEntity(
            questionId = UUID.randomUUID().toString(),
            session = session,
            mode = QuizMode.MULTIPLE_CHOICE,
            quoteText = quote.text,
            correctAuthor = quote.author,
            progress = progress,
            optionOne = options[0],
            optionTwo = options[1],
            optionThree = options[2],
        )
    }

    private fun isBinaryAnswerCorrect(
        question: QuizSessionQuestionEntity,
        answer: Boolean?,
    ): Boolean {
        require(answer != null) { "Binary answer is required for binary questions." }
        val proposedIsCorrect = question.proposedAuthor == question.correctAuthor
        return answer == proposedIsCorrect
    }

    private fun isMultipleChoiceAnswerCorrect(
        question: QuizSessionQuestionEntity,
        selectedOptionId: String?,
    ): Boolean {
        require(!selectedOptionId.isNullOrBlank()) { "Selected option is required for multiple-choice questions." }
        val selectedAuthor = question.optionLabelById(selectedOptionId)
        require(selectedAuthor != null) { "Selected option does not belong to the question." }
        return selectedAuthor == question.correctAuthor
    }

    private fun QuizSessionQuestionEntity.toDto(): QuizQuestionDto =
        QuizQuestionDto(
            id = questionId,
            quote = quoteText,
            mode = mode,
            progress = progress,
            totalQuestions = TOTAL_QUESTIONS,
            proposedAuthor = proposedAuthor,
            options = optionIds.zip(options).map { (id, label) -> QuizOptionDto(id = id, label = label) },
        )

    private fun QuizSessionQuestionEntity.optionLabelById(optionId: String): String? =
        optionIds.zip(options).firstOrNull { (id, _) -> id == optionId }?.second

    private fun QuizSessionEntity.toResultDto(): QuizResultDto {
        val incorrectAnswers = TOTAL_QUESTIONS - correctAnswers
        return QuizResultDto(
            mode = mode,
            totalQuestions = TOTAL_QUESTIONS,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            percentageScore = (correctAnswers * 100) / TOTAL_QUESTIONS,
        )
    }

    private companion object {
        const val TOTAL_QUESTIONS = 10
        val optionIds = listOf("A", "B", "C")
    }
}
