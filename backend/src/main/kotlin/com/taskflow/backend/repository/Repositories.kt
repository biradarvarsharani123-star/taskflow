package com.taskflow.backend.repository

import com.taskflow.backend.model.*
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByOwnerId(ownerId: Long): List<Project>
}

interface TaskRepository : JpaRepository<Task, Long>