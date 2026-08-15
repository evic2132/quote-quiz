package dev.elelan.quote_quiz_server

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CorsConfig(
    @Value("\${quotequiz.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*,https://localhost:*,https://127.0.0.1:*,https://quote-quiz.onrender.com,https://*.github.io}")
    private val allowedOriginPatterns: String,
) {
    val allowedOriginPatternsList: List<String>
        get() = allowedOriginPatterns
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
}