package com.taskflow.backend.service

import com.taskflow.backend.model.*
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

interface TaskFilterStrategy {
    fun toSpecification(): Specification<Task>
}

class ProjectFilter(private val projectId: Long) : TaskFilterStrategy {
    override fun toSpecification() = Specification<Task> { root, _, cb ->
        cb.equal(root.get<Project>("project").get<Long>("id"), projectId)
    }
}

class StatusFilter(private val status: Status) : TaskFilterStrategy {
    override fun toSpecification() = Specification<Task> { root, _, cb ->
        cb.equal(root.get<Status>("status"), status)
    }
}

class PriorityFilter(private val priority: Priority) : TaskFilterStrategy {
    override fun toSpecification() = Specification<Task> { root, _, cb ->
        cb.equal(root.get<Priority>("priority"), priority)
    }
}

class AssigneeFilter(private val email: String) : TaskFilterStrategy {
    override fun toSpecification() = Specification<Task> { root, _, cb ->
        cb.equal(root.get<User>("assignee").get<String>("email"), email)
    }
}

interface TaskSortStrategy {
    fun sort(): Sort
    fun toSpecification(): Specification<Task>? = null

    companion object {
        fun from(sortBy: String?, direction: String?): TaskSortStrategy {
            val asc = !direction.equals("desc", ignoreCase = true)
            return when (sortBy) {
                "priority" -> PrioritySort(asc)
                "dueDate" -> DueDateSort(asc)
                else -> DefaultSort
            }
        }
    }
}

class DueDateSort(private val asc: Boolean) : TaskSortStrategy {
    override fun sort(): Sort =
        Sort.by(if (asc) Sort.Direction.ASC else Sort.Direction.DESC, "dueDate")
}

class PrioritySort(private val asc: Boolean) : TaskSortStrategy {
    override fun sort(): Sort = Sort.unsorted()

    override fun toSpecification() = Specification<Task> { root, query, cb ->
        if (query != null && query.resultType != Long::class.javaObjectType) {
            val rank = cb.selectCase<Int>()
                .`when`(cb.equal(root.get<Priority>("priority"), Priority.LOW), 1)
                .`when`(cb.equal(root.get<Priority>("priority"), Priority.MEDIUM), 2)
                .otherwise(3)
            query.orderBy(if (asc) cb.asc(rank) else cb.desc(rank))
        }
        null
    }
}

object DefaultSort : TaskSortStrategy {
    override fun sort(): Sort = Sort.by(Sort.Direction.DESC, "createdAt")
}