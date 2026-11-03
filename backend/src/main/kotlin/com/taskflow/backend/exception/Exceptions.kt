package com.taskflow.backend.exception

class FieldValidationException(val field: String, override val message: String) : RuntimeException(message)