import { api } from './client'
import type { Project, Summary } from '../types'

export async function getProjects() {
    const { data } = await api.get<Project[]>('/projects')
    return data
}

export async function createProject(input: { name: string; description?: string }) {
    const { data } = await api.post<Project>('/projects', input)
    return data
}

export async function getSummary(projectId: number) {
    const { data } = await api.get<Summary>(`/projects/${projectId}/summary`)
    return data
}