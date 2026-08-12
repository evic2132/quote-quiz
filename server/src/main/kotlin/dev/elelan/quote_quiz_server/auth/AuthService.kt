package dev.elelan.quote_quiz_server.auth

import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.contract.auth.UserDto
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {

    fun login(email: String, password: String): LoginResponse {
        val authentication =
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password),
            )

        val principal = authentication.principal as AppUserPrincipal
        return LoginResponse(
            token = jwtService.createToken(principal.username),
            user =
                UserDto(
                    id = principal.id,
                    name = principal.displayName,
                    email = principal.username,
                ),
        )
    }
}
