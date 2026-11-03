export type Status = 'TODO' | 'IN_PROGRESS' | 'DONE'
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH'

export interface AuthResponse {
    token: string
    name: string
    email: string
}

export interface Project {
    id: number
    name: string
    description: string | null
    ownerName: string
    createdAt: string
}

export interface Task {
    id: number
    title: string
    description: string | null
    status: Status
    priority: Priority
    dueDate: string | null
    assigneeName: string | null
    assigneeEmail: string | null
    projectId: number
    overdue: boolean
    createdAt: string
    updatedAt: string
}

export interface TaskInput {
    title: string
    description?: string | null
    priority: Priority
    status: Status
    dueDate?: string | null
    assigneeEmail?: string | null
}

export interface PageResponse<T> {
    content: T[]
    page: number
    size: number
    totalElements: number
    totalPages: number
}

export interface Summary {
    todo: number
    inProgress: number
    done: number
    overdue: number
}

export interface TaskFilters {
    status?: Status
    priority?: Priority
    assigneeEmail?: string
    sortBy?: string
    direction?: 'asc' | 'desc'
    page?: number
    size?: number
}