package dev.elelan.quote_quiz_server.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtService(
    @Value("\${quotequiz.security.jwt-secret}")
    secret: String,
) {

    private val signingKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun createToken(subject: String): String {
        val now = Date()
        val expiresAt = Date(now.time + TOKEN_TTL_MILLIS)

        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiresAt)
            .signWith(signingKey)
            .compact()
    }

    fun extractSubject(token: String): String =
        parseClaims(token).subject

    fun isTokenValid(token: String, expectedSubject: String): Boolean {
        val claims = parseClaims(token)
        return claims.subject == expectedSubject && claims.expiration.after(Date())
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload

    private companion object {
        const val TOKEN_TTL_MILLIS = 24 * 60 * 60 * 1000L
    }
}
