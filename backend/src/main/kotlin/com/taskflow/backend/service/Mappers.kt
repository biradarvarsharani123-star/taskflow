package com.taskflow.backend.service

import com.taskflow.backend.dto.ProjectResponse
import com.taskflow.backend.dto.TaskResponse
import com.taskflow.backend.model.Project
import com.taskflow.backend.model.Status
import com.taskflow.backend.model.Task
import java.time.LocalDate

fun Project.toResponse() = ProjectResponse(
    id = id!!,
    name = name,
    description = description,
    ownerName = owner.name,
    createdAt = createdAt
)

fun Task.toResponse(today: LocalDate = LocalDate.now()) = TaskResponse(
    id = id!!,
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    assigneeName = assignee?.name,
    assigneeEmail = assignee?.email,
    projectId = project.id!!,
    overdue = status != Status.DONE && dueDate?.isBefore(today) == true,
    createdAt = createdAt,
    updatedAt = updatedAt
)