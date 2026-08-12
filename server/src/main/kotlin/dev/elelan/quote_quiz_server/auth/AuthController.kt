package dev.elelan.quote_quiz_server.auth

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.contract.auth.UserDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/auth/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): LoginResponse = authService.login(request.email, request.password)

    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal principal: AppUserPrincipal,
    ): UserDto =
        UserDto(
            id = principal.id,
            name = principal.displayName,
            email = principal.username,
        )
}
