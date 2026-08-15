package dev.elelan.quote_quiz_server.bootstrap

import dev.elelan.quote_quiz_server.quote.QuoteEntity
import dev.elelan.quote_quiz_server.quote.QuoteRepository
import dev.elelan.quote_quiz_server.user.UserEntity
import dev.elelan.quote_quiz_server.user.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import tools.jackson.databind.ObjectMapper

@Configuration
class DemoDataInitializer(
    private val objectMapper: ObjectMapper,
    @Value("classpath:data/famous_quotes.json")
    private val quotesJsonResource: Resource,
) {

    @Bean
    fun seedDemoData(
        userRepository: UserRepository,
        quoteRepository: QuoteRepository,
        @Value("\${quotequiz.seed.demo-user-password}")
        demoUserPassword: String,
        @Value("\${quotequiz.seed.reviewer-user-password}")
        reviewerUserPassword: String,
    ): ApplicationRunner =
        ApplicationRunner {
            if (userRepository.count() == 0L) {
                val passwordEncoder = BCryptPasswordEncoder()
                userRepository.saveAll(
                    listOf(
                        UserEntity(
                            name = "Demo User",
                            email = "demo@example.com",
                            passwordHash = passwordEncoder.encodeRequired(demoUserPassword),
                        ),
                        UserEntity(
                            name = "Reviewer User",
                            email = "reviewer@example.com",
                            passwordHash = passwordEncoder.encodeRequired(reviewerUserPassword),
                        ),
                    ),
                )
            }

            if (quoteRepository.count() == 0L) {
                quoteRepository.saveAll(DEMO_QUOTES)

                val quotes = loadQuotesFromJson()
                quoteRepository.saveAll(quotes)
            }
        }

    private fun loadQuotesFromJson(): List<QuoteEntity> {
        if (!quotesJsonResource.exists()) return emptyList()

        val seedDtos = objectMapper.readValue(
            quotesJsonResource.inputStream,
            Array<QuoteSeedDto>::class.java,
        )

        return seedDtos.map { dto ->
            QuoteEntity(
                text = dto.text,
                author = dto.author,
            )
        }
    }

    private companion object {
        val DEMO_QUOTES =
            listOf(
                QuoteEntity(text = "Be yourself; everyone else is already taken.", author = "Oscar Wilde"),
                QuoteEntity(text = "Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.", author = "Albert Einstein"),
                QuoteEntity(text = "So many books, so little time.", author = "Frank Zappa"),
                QuoteEntity(text = "A room without books is like a body without a soul.", author = "Marcus Tullius Cicero"),
                QuoteEntity(text = "Be the change that you wish to see in the world.", author = "Mahatma Gandhi"),
                QuoteEntity(text = "If you tell the truth, you don't have to remember anything.", author = "Mark Twain"),
                QuoteEntity(text = "Always forgive your enemies; nothing annoys them so much.", author = "Oscar Wilde"),
                QuoteEntity(text = "Live as if you were to die tomorrow. Learn as if you were to live forever.", author = "Mahatma Gandhi"),
                QuoteEntity(text = "Without music, life would be a mistake.", author = "Friedrich Nietzsche"),
                QuoteEntity(text = "We accept the love we think we deserve.", author = "Stephen Chbosky"),
                QuoteEntity(text = "Imperfection is beauty, madness is genius and it's better to be absolutely ridiculous than absolutely boring.", author = "Marilyn Monroe"),
                QuoteEntity(text = "It is never too late to be what you might have been.", author = "George Eliot"),
                QuoteEntity(text = "Do what you can, with what you have, where you are.", author = "Theodore Roosevelt"),
                QuoteEntity(text = "Everything you can imagine is real.", author = "Pablo Picasso"),
                QuoteEntity(text = "Happiness depends upon ourselves.", author = "Aristotle"),
                QuoteEntity(text = "Turn your wounds into wisdom.", author = "Oprah Winfrey"),
                QuoteEntity(text = "Try to be a rainbow in someone's cloud.", author = "Maya Angelou"),
                QuoteEntity(text = "In the middle of difficulty lies opportunity.", author = "Albert Einstein"),
                QuoteEntity(text = "The only true wisdom is in knowing you know nothing.", author = "Socrates"),
                QuoteEntity(text = "The purpose of our lives is to be happy.", author = "Dalai Lama"),
                QuoteEntity(text = "Life is what happens when you're busy making other plans.", author = "John Lennon"),
                QuoteEntity(text = "Get busy living or get busy dying.", author = "Stephen King"),
                QuoteEntity(text = "You only live once, but if you do it right, once is enough.", author = "Mae West"),
                QuoteEntity(text = "Many of life's failures are people who did not realize how close they were to success when they gave up.", author = "Thomas A. Edison"),
                QuoteEntity(text = "If life were predictable it would cease to be life, and be without flavor.", author = "Eleanor Roosevelt"),
                QuoteEntity(text = "The future belongs to those who believe in the beauty of their dreams.", author = "Eleanor Roosevelt"),
                QuoteEntity(text = "Tell me and I forget. Teach me and I remember. Involve me and I learn.", author = "Benjamin Franklin"),
                QuoteEntity(text = "Whoever is happy will make others happy too.", author = "Anne Frank"),
                QuoteEntity(text = "You miss 100 percent of the shots you never take.", author = "Wayne Gretzky"),
                QuoteEntity(text = "It does not matter how slowly you go as long as you do not stop.", author = "Confucius"),
            )
    }
}

private fun BCryptPasswordEncoder.encodeRequired(rawPassword: String): String =
    requireNotNull(encode(rawPassword)) {
        "BCrypt encoder returned a null password hash."
    }

private data class QuoteSeedDto(
    val text: String = "",
    val author: String = "",
)