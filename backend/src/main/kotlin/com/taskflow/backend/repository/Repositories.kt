package com.taskflow.backend.repository

import com.taskflow.backend.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByOwnerId(ownerId: Long): List<Project>
}

interface TaskRepository : JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    fun countByProjectIdAndStatus(projectId: Long, status: Status): Long

    @Query(
        "select count(t) from Task t where t.project.id = :projectId " +
                "and t.status <> com.taskflow.backend.model.Status.DONE " +
                "and t.dueDate is not null and t.dueDate < :today"
    )
    fun countOverdue(@Param("projectId") projectId: Long, @Param("today") today: LocalDate): Long
}