import { api } from './client'
import type { PageResponse, Status, Task, TaskFilters, TaskInput } from '../types'

export async function getTasks(projectId: number, filters: TaskFilters = {}) {
    const { data } = await api.get<PageResponse<Task>>(`/projects/${projectId}/tasks`, {
        params: { size: 100, ...filters },
    })
    return data
}

export async function createTask(projectId: number, input: TaskInput) {
    const { data } = await api.post<Task>(`/projects/${projectId}/tasks`, input)
    return data
}

export async function updateTask(taskId: number, input: TaskInput) {
    const { data } = await api.put<Task>(`/tasks/${taskId}`, input)
    return data
}

export async function updateTaskStatus(taskId: number, status: Status) {
    const { data } = await api.patch<Task>(`/tasks/${taskId}/status`, { status })
    return data
}

export async function deleteTask(taskId: number) {
    await api.delete(`/tasks/${taskId}`)
}