package dev.elelan.quote_quiz_server.bootstrap

import dev.elelan.quote_quiz_server.quote.QuoteRepository
import dev.elelan.quote_quiz_server.user.UserRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoDataInitializerTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var quoteRepository: QuoteRepository

    @Test
    fun `startup seeds demo users and quotes`() {
        assertEquals(2L, userRepository.count())
        assertTrue(quoteRepository.count() >= 20L)
        assertTrue(userRepository.existsByEmail("demo@example.com"))
        assertTrue(userRepository.existsByEmail("reviewer@example.com"))
    }
}
