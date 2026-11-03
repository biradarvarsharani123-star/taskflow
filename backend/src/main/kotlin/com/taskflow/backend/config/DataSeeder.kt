package com.taskflow.backend.config

import com.taskflow.backend.model.*
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (userRepository.existsByEmail("demo@taskflow.com")) return

        val demo = userRepository.save(
            User(name = "Demo User", email = "demo@taskflow.com", password = passwordEncoder.encode("Demo@123")!!)
        )
        val teammate = userRepository.save(
            User(name = "Teammate", email = "teammate@taskflow.com", password = passwordEncoder.encode("Demo@123")!!)
        )
        val project = projectRepository.save(
            Project(name = "Website Redesign", description = "Sample project", owner = demo)
        )
        val today = LocalDate.now()
        taskRepository.saveAll(
            listOf(
                Task(title = "Design homepage", priority = Priority.HIGH, status = Status.IN_PROGRESS,
                    dueDate = today.plusDays(3), assignee = demo, project = project),
                Task(title = "Write copy", priority = Priority.MEDIUM, status = Status.TODO,
                    dueDate = today.plusDays(7), assignee = teammate, project = project),
                Task(title = "Fix old bug", priority = Priority.HIGH, status = Status.TODO,
                    dueDate = today.minusDays(2), project = project),
                Task(title = "Set up hosting", priority = Priority.LOW, status = Status.DONE, project = project)
            )
        )
    }
}