package com.taskflow.backend.security

import com.taskflow.backend.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val email = jwtService.extractEmail(header.substring(7))
            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val user = userRepository.findByEmail(email)
                if (user != null) {
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(user.email, null, emptyList())
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}