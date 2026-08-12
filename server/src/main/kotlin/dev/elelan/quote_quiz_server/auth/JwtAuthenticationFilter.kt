package dev.elelan.quote_quiz_server.auth

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: AppUserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        try {
            val username = jwtService.extractSubject(token)
            if (username.isNotBlank() && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                if (jwtService.isTokenValid(token, userDetails.username)) {
                    val authentication =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (_: JwtException) {
            SecurityContextHolder.clearContext()
        } catch (_: IllegalArgumentException) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/h2-console")
    }
}
