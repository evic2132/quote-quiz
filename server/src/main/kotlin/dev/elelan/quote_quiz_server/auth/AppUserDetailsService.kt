package dev.elelan.quote_quiz_server.auth

import dev.elelan.quote_quiz_server.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username)
            ?.let(AppUserPrincipal::fromUser)
            ?: throw UsernameNotFoundException("User not found: $username")
}
