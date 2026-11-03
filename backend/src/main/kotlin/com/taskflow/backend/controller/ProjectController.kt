package com.taskflow.backend.controller

import com.taskflow.backend.dto.ProjectRequest
import com.taskflow.backend.service.ProjectService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(private val projectService: ProjectService) {

    @GetMapping
    fun list(auth: Authentication) = projectService.list(auth.name)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(auth: Authentication, @Valid @RequestBody req: ProjectRequest) =
        projectService.create(auth.name, req)
}