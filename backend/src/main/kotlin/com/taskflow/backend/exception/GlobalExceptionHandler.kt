package com.taskflow.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

data class FieldError(val field: String, val message: String)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val errors: List<FieldError> = emptyList(),
    val timestamp: Instant = Instant.now()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    private fun build(status: HttpStatus, message: String, errors: List<FieldError> = emptyList()) =
        ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, message, errors)
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { FieldError(it.field, it.defaultMessage ?: "Invalid value") }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors)
    }

    @ExceptionHandler(FieldValidationException::class)
    fun handleField(ex: FieldValidationException) =
        build(HttpStatus.BAD_REQUEST, "Validation failed", listOf(FieldError(ex.field, ex.message)))

    @ExceptionHandler(ResponseStatusException::class)
    fun handleStatus(ex: ResponseStatusException) =
        build(HttpStatus.valueOf(ex.statusCode.value()), ex.reason ?: "Request failed")

    @ExceptionHandler(HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class)
    fun handleBadRequest(ex: Exception) =
        build(HttpStatus.BAD_REQUEST, "Malformed request or invalid value")

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception) =
        build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong")
}