package com.taskflow.backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email is not valid")
    val email: String,
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email is required") val email: String,
    @field:NotBlank(message = "Password is required") val password: String
)

data class AuthResponse(val token: String, val name: String, val email: String)