package com.taskflow.backend.service

import com.taskflow.backend.dto.*
import com.taskflow.backend.exception.FieldValidationException
import com.taskflow.backend.model.*
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDate

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {
    private fun notFound(what: String) = ResponseStatusException(HttpStatus.NOT_FOUND, "$what not found")

    private fun ownedProject(projectId: Long, email: String): Project {
        val project = projectRepository.findById(projectId).orElseThrow { notFound("Project") }
        if (project.owner.email != email) throw notFound("Project")
        return project
    }

    private fun ownedTask(taskId: Long, email: String): Task {
        val task = taskRepository.findById(taskId).orElseThrow { notFound("Task") }
        if (task.project.owner.email != email) throw notFound("Task")
        return task
    }

    private fun resolveAssignee(email: String?): User? {
        if (email.isNullOrBlank()) return null
        return userRepository.findByEmail(email.trim())
            ?: throw FieldValidationException("assigneeEmail", "No user with this email")
    }

    @Transactional(readOnly = true)
    fun list(
        email: String, projectId: Long, status: Status?, priority: Priority?,
        assigneeEmail: String?, sortBy: String?, direction: String?, page: Int, size: Int
    ): PageResponse<TaskResponse> {
        val project = ownedProject(projectId, email)

        val filters = listOfNotNull<TaskFilterStrategy>(
            ProjectFilter(project.id!!),
            status?.let { StatusFilter(it) },
            priority?.let { PriorityFilter(it) },
            assigneeEmail?.takeIf { it.isNotBlank() }?.let { AssigneeFilter(it) }
        )
        var spec = filters.map { it.toSpecification() }.reduce { a, b -> a.and(b) }

        val sortStrategy = TaskSortStrategy.from(sortBy, direction)
        sortStrategy.toSpecification()?.let { spec = spec.and(it) }

        val pageable = PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, 100), sortStrategy.sort())
        val result = taskRepository.findAll(spec, pageable)
        val today = LocalDate.now()
        return PageResponse(
            content = result.content.map { it.toResponse(today) },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )
    }

    fun create(email: String, projectId: Long, req: TaskRequest): TaskResponse {
        val project = ownedProject(projectId, email)
        if (req.dueDate != null && req.dueDate.isBefore(LocalDate.now())) {
            throw FieldValidationException("dueDate", "Due date cannot be in the past")
        }
        val task = taskRepository.save(
            Task(
                title = req.title.trim(),
                description = req.description,
                status = req.status,
                priority = req.priority,
                dueDate = req.dueDate,
                assignee = resolveAssignee(req.assigneeEmail),
                project = project
            )
        )
        return task.toResponse()
    }

    fun update(email: String, taskId: Long, req: TaskRequest): TaskResponse {
        val task = ownedTask(taskId, email)
        task.title = req.title.trim()
        task.description = req.description
        task.priority = req.priority
        task.status = req.status
        task.dueDate = req.dueDate
        task.assignee = resolveAssignee(req.assigneeEmail)
        task.updatedAt = Instant.now()
        return taskRepository.save(task).toResponse()
    }

    fun updateStatus(email: String, taskId: Long, status: Status): TaskResponse {
        val task = ownedTask(taskId, email)
        task.status = status
        task.updatedAt = Instant.now()
        return taskRepository.save(task).toResponse()
    }

    fun delete(email: String, taskId: Long) {
        taskRepository.delete(ownedTask(taskId, email))
    }

    @Transactional(readOnly = true)
    fun summary(email: String, projectId: Long): SummaryResponse {
        val project = ownedProject(projectId, email)
        val id = project.id!!
        return SummaryResponse(
            todo = taskRepository.countByProjectIdAndStatus(id, Status.TODO),
            inProgress = taskRepository.countByProjectIdAndStatus(id, Status.IN_PROGRESS),
            done = taskRepository.countByProjectIdAndStatus(id, Status.DONE),
            overdue = taskRepository.countOverdue(id, LocalDate.now())
        )
    }
}