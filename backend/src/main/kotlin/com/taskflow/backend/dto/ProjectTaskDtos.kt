package com.taskflow.backend.dto

import com.taskflow.backend.model.Priority
import com.taskflow.backend.model.Status
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.LocalDate

data class ProjectRequest(
    @field:NotBlank(message = "Project name is required")
    @field:Size(max = 120, message = "Project name must be at most 120 characters")
    val name: String,
    @field:Size(max = 1000, message = "Description must be at most 1000 characters")
    val description: String? = null
)

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val ownerName: String,
    val createdAt: Instant
)

data class TaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 120, message = "Title must be at most 120 characters")
    val title: String,
    @field:Size(max = 2000, message = "Description must be at most 2000 characters")
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.TODO,
    val dueDate: LocalDate? = null,
    val assigneeEmail: String? = null
)

data class StatusRequest(val status: Status)

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: Status,
    val priority: Priority,
    val dueDate: LocalDate?,
    val assigneeName: String?,
    val assigneeEmail: String?,
    val projectId: Long,
    val overdue: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

data class SummaryResponse(
    val todo: Long,
    val inProgress: Long,
    val done: Long,
    val overdue: Long
)