import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { getSummary } from '../api/projects'
import { createTask, deleteTask, getTasks, updateTask, updateTaskStatus } from '../api/tasks'
import { getErrorMessage } from '../api/client'
import { PRIORITIES, STATUSES } from '../constants'
import type { Priority, Status, Task, TaskFilters, TaskInput } from '../types'
import Spinner from '../components/Spinner'
import ErrorMessage from '../components/ErrorMessage'
import EmptyState from '../components/EmptyState'
import SummaryBar from '../components/SummaryBar'
import TaskCard from '../components/TaskCard'
import TaskForm from '../components/TaskForm'
import Modal from '../components/Modal'

type FormState = { mode: 'create' } | { mode: 'edit'; task: Task } | null

export default function ProjectBoardPage() {
    const { id } = useParams()
    const projectId = Number(id)
    const queryClient = useQueryClient()

    const [priority, setPriority] = useState<Priority | ''>('')
    const [assignee, setAssignee] = useState('')
    const [direction, setDirection] = useState<'asc' | 'desc'>('asc')
    const [form, setForm] = useState<FormState>(null)

    const filters: TaskFilters = {
        priority: priority || undefined,
        assigneeEmail: assignee.trim() || undefined,
        sortBy: 'dueDate',
        direction,
    }

    const tasks = useQuery({
        queryKey: ['tasks', projectId, filters],
        queryFn: () => getTasks(projectId, filters),
    })
    const summary = useQuery({
        queryKey: ['summary', projectId],
        queryFn: () => getSummary(projectId),
    })

    const refresh = () => {
        queryClient.invalidateQueries({ queryKey: ['tasks', projectId] })
        queryClient.invalidateQueries({ queryKey: ['summary', projectId] })
    }

    const changeStatus = useMutation({
        mutationFn: ({ taskId, status }: { taskId: number; status: Status }) =>
            updateTaskStatus(taskId, status),
        onSuccess: refresh,
    })

    const save = useMutation({
        mutationFn: (input: TaskInput) =>
            form?.mode === 'edit' ? updateTask(form.task.id, input) : createTask(projectId, input),
        onSuccess: () => {
            refresh()
            setForm(null)
        },
    })

    const remove = useMutation({
        mutationFn: (taskId: number) => deleteTask(taskId),
        onSuccess: refresh,
    })

    const items = tasks.data?.content ?? []

    const confirmDelete = (task: Task) => {
        if (window.confirm(`Delete "${task.title}"?`)) remove.mutate(task.id)
    }

    const openCreate = () => {
        save.reset()
        setForm({ mode: 'create' })
    }

    const openEdit = (task: Task) => {
        save.reset()
        setForm({ mode: 'edit', task })
    }

    return (
        <div className="container">
            <header className="topbar">
                <div>
                    <Link to="/projects" className="muted">
                        ← All projects
                    </Link>
                    <h1>Project board</h1>
                </div>
                <button className="btn btn-primary" onClick={openCreate}>
                    + New task
                </button>
            </header>

            {summary.isError && (
                <ErrorMessage message={getErrorMessage(summary.error)} onRetry={() => summary.refetch()} />
            )}
            {summary.data && <SummaryBar summary={summary.data} />}

            <div className="filters">
                <label>
                    Priority
                    <select value={priority} onChange={(e) => setPriority(e.target.value as Priority | '')}>
                        <option value="">All</option>
                        {PRIORITIES.map((p) => (
                            <option key={p.value} value={p.value}>
                                {p.label}
                            </option>
                        ))}
                    </select>
                </label>
                <label>
                    Assignee email
                    <input
                        value={assignee}
                        onChange={(e) => setAssignee(e.target.value)}
                        placeholder="demo@taskflow.com"
                    />
                </label>
                <label>
                    Sort by due date
                    <select value={direction} onChange={(e) => setDirection(e.target.value as 'asc' | 'desc')}>
                        <option value="asc">Earliest first</option>
                        <option value="desc">Latest first</option>
                    </select>
                </label>
            </div>

            {changeStatus.isError && (
                <div className="alert alert-error">{getErrorMessage(changeStatus.error)}</div>
            )}
            {remove.isError && <div className="alert alert-error">{getErrorMessage(remove.error)}</div>}

            {tasks.isLoading && <Spinner label="Loading tasks..." />}
            {tasks.isError && (
                <ErrorMessage message={getErrorMessage(tasks.error)} onRetry={() => tasks.refetch()} />
            )}
            {tasks.data && items.length === 0 && (
                <EmptyState title="No tasks found" hint="Create a task or change the filters." />
            )}

            {tasks.data && items.length > 0 && (
                <div className="board">
                    {STATUSES.map((col) => {
                        const colTasks = items.filter((t) => t.status === col.value)
                        return (
                            <section key={col.value} className="column">
                                <h2>
                                    {col.label} <span className="count">{colTasks.length}</span>
                                </h2>
                                {colTasks.length === 0 && <p className="muted">No tasks</p>}
                                {colTasks.map((task) => (
                                    <TaskCard
                                        key={task.id}
                                        task={task}
                                        disabled={changeStatus.isPending}
                                        onStatusChange={(status) => changeStatus.mutate({ taskId: task.id, status })}
                                        onEdit={() => openEdit(task)}
                                        onDelete={() => confirmDelete(task)}
                                    />
                                ))}
                            </section>
                        )
                    })}
                </div>
            )}

            {form && (
                <Modal title={form.mode === 'edit' ? 'Edit task' : 'New task'} onClose={() => setForm(null)}>
                    <TaskForm
                        initial={form.mode === 'edit' ? form.task : undefined}
                        submitting={save.isPending}
                        serverError={save.isError ? getErrorMessage(save.error) : undefined}
                        onSubmit={(input) => save.mutate(input)}
                        onCancel={() => setForm(null)}
                    />
                </Modal>
            )}
        </div>
    )
}