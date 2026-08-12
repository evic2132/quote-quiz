package dev.elelan.quote_quiz_server.user

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `findByEmail returns matching user`() {
        val savedUser =
            userRepository.save(
                UserEntity(
                    name = "Alice",
                    email = "alice@example.com",
                    passwordHash = "hash",
                ),
            )

        val foundUser = userRepository.findByEmail("alice@example.com")

        assertNotNull(foundUser)
        assertEquals(savedUser.id, foundUser.id)
        assertEquals("Alice", foundUser.name)
    }

    @Test
    fun `saving duplicate email fails`() {
        userRepository.saveAndFlush(
            UserEntity(
                name = "Alice",
                email = "duplicate@example.com",
                passwordHash = "hash1",
            ),
        )

        org.junit.jupiter.api.assertThrows<DataIntegrityViolationException> {
            userRepository.saveAndFlush(
                UserEntity(
                    name = "Bob",
                    email = "duplicate@example.com",
                    passwordHash = "hash2",
                ),
            )
        }
    }
}
