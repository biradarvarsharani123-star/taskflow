package com.taskflow.backend.controller

import com.taskflow.backend.dto.StatusRequest
import com.taskflow.backend.dto.TaskRequest
import com.taskflow.backend.model.Priority
import com.taskflow.backend.model.Status
import com.taskflow.backend.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TaskController(private val taskService: TaskService) {

    @GetMapping("/projects/{projectId}/tasks")
    fun list(
        auth: Authentication,
        @PathVariable projectId: Long,
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) assignee: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) direction: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ) = taskService.list(auth.name, projectId, status, priority, assignee, sortBy, direction, page, size)

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(auth: Authentication, @PathVariable projectId: Long, @Valid @RequestBody req: TaskRequest) =
        taskService.create(auth.name, projectId, req)

    @PutMapping("/tasks/{taskId}")
    fun update(auth: Authentication, @PathVariable taskId: Long, @Valid @RequestBody req: TaskRequest) =
        taskService.update(auth.name, taskId, req)

    @PatchMapping("/tasks/{taskId}/status")
    fun updateStatus(auth: Authentication, @PathVariable taskId: Long, @RequestBody req: StatusRequest) =
        taskService.updateStatus(auth.name, taskId, req.status)

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(auth: Authentication, @PathVariable taskId: Long) = taskService.delete(auth.name, taskId)

    @GetMapping("/projects/{projectId}/summary")
    fun summary(auth: Authentication, @PathVariable projectId: Long) =
        taskService.summary(auth.name, projectId)
}