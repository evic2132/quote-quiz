package dev.elelan.quote_quiz_server.auth

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

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `login succeeds for seeded demo user`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "demo@example.com",
                      "password": "password123"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isString)
            .andExpect(jsonPath("$.user.email").value("demo@example.com"))
            .andExpect(jsonPath("$.user.name").value("Demo User"))
    }

    @Test
    fun `login fails for unknown user`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "missing@example.com",
                      "password": "password123"
                    }
                    """.trimIndent(),
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
                    """
                    {
                      "email": "demo@example.com",
                      "password": "wrong-password"
                    }
                    """.trimIndent(),
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
        val loginResponse =
            mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "email": "reviewer@example.com",
                          "password": "reviewer123"
                        }
                        """.trimIndent(),
                    ),
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        val token = "\"token\":\"([^\"]+)\"".toRegex().find(loginResponse)?.groupValues?.get(1)
            ?: error("Token missing from login response")

        mockMvc.perform(
            get("/api/v1/me")
                .header("Authorization", "Bearer $token"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("reviewer@example.com"))
            .andExpect(jsonPath("$.name").value("Reviewer User"))
    }
}
