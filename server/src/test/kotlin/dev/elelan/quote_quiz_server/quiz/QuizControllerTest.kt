package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizSessionDto
import dev.elelan.quotequiz.contract.quiz.QuizSessionStartRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerRequest
import dev.elelan.quotequiz.contract.quiz.SubmitAnswerResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `create session requires authentication`() {
        mockMvc.perform(
            post("/api/v1/quiz/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(QuizSessionStartRequest(mode = QuizMode.BINARY))),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
    }

    @Test
    fun `create session returns first question for authenticated user`() {
        val token = loginAndExtractToken("demo@example.com", "password123")

        val resultJson =
            mockMvc.perform(
                post("/api/v1/quiz/sessions")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(QuizSessionStartRequest(mode = QuizMode.BINARY))),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val sessionDto = objectMapper.readValue(resultJson, QuizSessionDto::class.java)

        assertNotNull(sessionDto.sessionId)
        assertEquals(QuizMode.BINARY, sessionDto.mode)
        assertEquals(10, sessionDto.totalQuestions)
        assertNotNull(sessionDto.currentQuestion.id)
        assertEquals(1, sessionDto.currentQuestion.progress)
    }

    @Test
    fun `submit answer returns next question`() {
        val token = loginAndExtractToken("demo@example.com", "password123")
        val session = startBinarySession(token)

        val resultJson =
            mockMvc.perform(
                post("/api/v1/quiz/sessions/${session.sessionId}/answers")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            SubmitAnswerRequest(
                                questionId = session.currentQuestion.id,
                                binaryAnswer = true,
                            ),
                        ),
                    ),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val response = objectMapper.readValue(resultJson, SubmitAnswerResponse::class.java)

        assertEquals(session.currentQuestion.id, response.questionId)
        assertEquals(false, response.completed)
        assertNotNull(response.nextQuestion?.id)
        assertEquals(2, response.nextQuestion?.progress)
    }

    @Test
    fun `submitting same question twice replays stored result`() {
        val token = loginAndExtractToken("demo@example.com", "password123")
        val session = startBinarySession(token)
        val request = SubmitAnswerRequest(questionId = session.currentQuestion.id, binaryAnswer = true)

        val firstResponseJson =
            mockMvc.perform(
                post("/api/v1/quiz/sessions/${session.sessionId}/answers")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val replayedResponseJson =
            mockMvc.perform(
                post("/api/v1/quiz/sessions/${session.sessionId}/answers")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val firstResponse = objectMapper.readValue(firstResponseJson, SubmitAnswerResponse::class.java)
        val replayedResponse = objectMapper.readValue(replayedResponseJson, SubmitAnswerResponse::class.java)

        assertEquals(firstResponse.questionId, replayedResponse.questionId)
        assertEquals(firstResponse.correct, replayedResponse.correct)
        assertEquals(firstResponse.score, replayedResponse.score)
        assertEquals(firstResponse.nextQuestion?.id, replayedResponse.nextQuestion?.id)
    }

    @Test
    fun `user cannot answer another user's session`() {
        val ownerToken = loginAndExtractToken("demo@example.com", "password123")
        val otherToken = loginAndExtractToken("reviewer@example.com", "reviewer123")
        val session = startBinarySession(ownerToken)

        mockMvc.perform(
            post("/api/v1/quiz/sessions/${session.sessionId}/answers")
                .header("Authorization", "Bearer $otherToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        SubmitAnswerRequest(
                            questionId = session.currentQuestion.id,
                            binaryAnswer = true,
                        ),
                    ),
                ),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("QUIZ_SESSION_NOT_FOUND"))
    }

    @Test
    fun `invalid answer payload returns bad request json`() {
        val token = loginAndExtractToken("demo@example.com", "password123")
        val session = startBinarySession(token)

        mockMvc.perform(
            post("/api/v1/quiz/sessions/${session.sessionId}/answers")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        SubmitAnswerRequest(
                            questionId = session.currentQuestion.id,
                            selectedOptionId = "wrong-shape-for-binary",
                        ),
                    ),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message").value("Invalid request body"))
    }

    private fun loginAndExtractToken(email: String, password: String): String {
        val responseJson =
            mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            LoginRequest(
                                email = email,
                                password = password,
                            ),
                        ),
                    ),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        return objectMapper.readValue(responseJson, LoginResponse::class.java).token
    }

    private fun startBinarySession(token: String): QuizSessionDto {
        val responseJson =
            mockMvc.perform(
                post("/api/v1/quiz/sessions")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(QuizSessionStartRequest(mode = QuizMode.BINARY))),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        return objectMapper.readValue(responseJson, QuizSessionDto::class.java)
    }
}
