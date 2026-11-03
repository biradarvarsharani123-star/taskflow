package com.taskflow.backend.service

import com.taskflow.backend.dto.ProjectRequest
import com.taskflow.backend.dto.ProjectResponse
import com.taskflow.backend.model.Project
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun list(email: String): List<ProjectResponse> {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
        return projectRepository.findAllByOwnerId(user.id!!).map { it.toResponse() }
    }

    fun create(email: String, req: ProjectRequest): ProjectResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
        val project = projectRepository.save(
            Project(name = req.name.trim(), description = req.description, owner = user)
        )
        return project.toResponse()
    }
}