package dev.elelan.quote_quiz_server.auth

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.contract.auth.UserDto
import kotlin.test.assertEquals
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
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `login succeeds for seeded demo user`() {
        val responseJson =
            mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            LoginRequest(
                                email = "demo@example.com",
                                password = "password123",
                            ),
                        ),
                    ),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val response = objectMapper.readValue(responseJson, LoginResponse::class.java)

        assertEquals("demo@example.com", response.user.email)
        assertEquals("Demo User", response.user.name)
    }

    @Test
    fun `login fails for unknown user`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        LoginRequest(
                            email = "missing@example.com",
                            password = "password123",
                        ),
                    ),
                ),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
    }

    @Test
    fun `login fails for wrong password`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        LoginRequest(
                            email = "demo@example.com",
                            password = "wrong-password",
                        ),
                    ),
                ),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
            .andExpect(jsonPath("$.message").value("Invalid credentials"))
    }

    @Test
    fun `me is unauthorized without token`() {
        mockMvc.perform(get("/api/v1/me"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("Authentication is required"))
    }

    @Test
    fun `me returns authenticated user`() {
        val token = loginAndExtractToken("reviewer@example.com", "reviewer123")

        val responseJson =
            mockMvc.perform(
                get("/api/v1/me")
                    .header("Authorization", "Bearer $token"),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val response = objectMapper.readValue(responseJson, UserDto::class.java)

        assertEquals("reviewer@example.com", response.email)
        assertEquals("Reviewer User", response.name)
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
}
