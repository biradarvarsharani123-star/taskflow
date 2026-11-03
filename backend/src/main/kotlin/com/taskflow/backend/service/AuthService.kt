package com.taskflow.backend.service

import com.taskflow.backend.dto.*
import com.taskflow.backend.model.User
import com.taskflow.backend.repository.UserRepository
import com.taskflow.backend.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun register(req: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(req.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }
        val user = userRepository.save(
            User(name = req.name, email = req.email, password = passwordEncoder.encode(req.password)!!)
        )
        return AuthResponse(jwtService.generateToken(user.email), user.name, user.email)
    }

    fun login(req: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        if (!passwordEncoder.matches(req.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        }
        return AuthResponse(jwtService.generateToken(user.email), user.name, user.email)
    }
}