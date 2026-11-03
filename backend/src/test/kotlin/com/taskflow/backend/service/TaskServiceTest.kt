package com.taskflow.backend.service

import com.taskflow.backend.dto.TaskRequest
import com.taskflow.backend.exception.FieldValidationException
import com.taskflow.backend.model.*
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.Optional

class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val projectRepository = mockk<ProjectRepository>()
    private val userRepository = mockk<UserRepository>()
    private lateinit var service: TaskService

    private val ownerEmail = "owner@test.com"
    private val owner = User(name = "Owner", email = ownerEmail, password = "x", id = 1L)
    private val project = Project(name = "P1", description = null, owner = owner, id = 10L)

    @BeforeEach
    fun setUp() {
        service = TaskService(taskRepository, projectRepository, userRepository)
        every { projectRepository.findById(10L) } returns Optional.of(project)
        every { taskRepository.save(any()) } answers {
            val t = firstArg<Task>()
            if (t.id != null) t else Task(
                title = t.title,
                description = t.description,
                status = t.status,
                priority = t.priority,
                dueDate = t.dueDate,
                assignee = t.assignee,
                project = t.project,
                id = 99L
            )
        }
    }

    private fun request(
        title: String = "Write tests",
        dueDate: LocalDate? = LocalDate.now().plusDays(3),
        assigneeEmail: String? = null
    ) = TaskRequest(
        title = title,
        description = "desc",
        status = Status.TODO,
        priority = Priority.HIGH,
        dueDate = dueDate,
        assigneeEmail = assigneeEmail
    )

    @Test
    fun `create saves task with trimmed title`() {
        val result = service.create(ownerEmail, 10L, request(title = "  Write tests  "))

        assertEquals("Write tests", result.title)
        verify(exactly = 1) { taskRepository.save(any()) }
    }

    @Test
    fun `create rejects due date in the past`() {
        val past = request(dueDate = LocalDate.now().minusDays(1))

        assertThrows<FieldValidationException> { service.create(ownerEmail, 10L, past) }
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun `create fails with not found when project belongs to someone else`() {
        val ex = assertThrows<ResponseStatusException> {
            service.create("other@test.com", 10L, request())
        }
        assertEquals(404, ex.statusCode.value())
    }

    @Test
    fun `create fails when assignee email does not exist`() {
        every { userRepository.findByEmail("ghost@test.com") } returns null

        assertThrows<FieldValidationException> {
            service.create(ownerEmail, 10L, request(assigneeEmail = "ghost@test.com"))
        }
    }

    @Test
    fun `updateStatus changes only the status`() {
        val task = Task(title = "T", project = project, id = 5L)
        every { taskRepository.findById(5L) } returns Optional.of(task)

        val result = service.updateStatus(ownerEmail, 5L, Status.DONE)

        assertEquals(Status.DONE, task.status)
        assertEquals("T", result.title)
    }

    @Test
    fun `updateStatus is hidden from non owners`() {
        val task = Task(title = "T", project = project, id = 5L)
        every { taskRepository.findById(5L) } returns Optional.of(task)

        assertThrows<ResponseStatusException> {
            service.updateStatus("other@test.com", 5L, Status.DONE)
        }
    }

    @Test
    fun `delete removes the task for the owner`() {
        val task = Task(title = "T", project = project, id = 5L)
        every { taskRepository.findById(5L) } returns Optional.of(task)
        every { taskRepository.delete(task) } returns Unit

        service.delete(ownerEmail, 5L)

        verify(exactly = 1) { taskRepository.delete(task) }
    }

    @Test
    fun `summary returns counts per status and overdue`() {
        every { taskRepository.countByProjectIdAndStatus(10L, Status.TODO) } returns 2L
        every { taskRepository.countByProjectIdAndStatus(10L, Status.IN_PROGRESS) } returns 1L
        every { taskRepository.countByProjectIdAndStatus(10L, Status.DONE) } returns 4L
        every { taskRepository.countOverdue(10L, any()) } returns 3L

        val result = service.summary(ownerEmail, 10L)

        assertEquals(2L, result.todo.toLong())
        assertEquals(1L, result.inProgress.toLong())
        assertEquals(4L, result.done.toLong())
        assertEquals(3L, result.overdue.toLong())
    }
}