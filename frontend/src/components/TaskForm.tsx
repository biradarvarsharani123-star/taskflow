import { useState, type FormEvent } from 'react'
import { PRIORITIES, STATUSES } from '../constants'
import type { Priority, Status, Task, TaskInput } from '../types'

function todayString() {
    const d = new Date()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${d.getFullYear()}-${month}-${day}`
}

type Errors = { title?: string; dueDate?: string }

export default function TaskForm({
                                     initial,
                                     submitting,
                                     serverError,
                                     onSubmit,
                                     onCancel,
                                 }: {
    initial?: Task
    submitting: boolean
    serverError?: string
    onSubmit: (input: TaskInput) => void
    onCancel: () => void
}) {
    const [title, setTitle] = useState(initial?.title ?? '')
    const [description, setDescription] = useState(initial?.description ?? '')
    const [priority, setPriority] = useState<Priority>(initial?.priority ?? 'MEDIUM')
    const [status, setStatus] = useState<Status>(initial?.status ?? 'TODO')
    const [dueDate, setDueDate] = useState(initial?.dueDate ?? '')
    const [assigneeEmail, setAssigneeEmail] = useState(initial?.assigneeEmail ?? '')
    const [errors, setErrors] = useState<Errors>({})

    const validate = () => {
        const next: Errors = {}
        if (!title.trim()) next.title = 'Title is required'
        else if (title.trim().length > 120) next.title = 'Title must be at most 120 characters'
        if (!initial && dueDate && dueDate < todayString()) {
            next.dueDate = 'Due date cannot be in the past'
        }
        setErrors(next)
        return Object.keys(next).length === 0
    }

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault()
        if (!validate()) return
        onSubmit({
            title: title.trim(),
            description: description.trim() || null,
            priority,
            status,
            dueDate: dueDate || null,
            assigneeEmail: assigneeEmail.trim() || null,
        })
    }

    return (
        <form className="form" onSubmit={handleSubmit} noValidate>
            {serverError && <div className="alert alert-error">{serverError}</div>}

            <label>
                Title
                <input value={title} onChange={(e) => setTitle(e.target.value)} maxLength={130} />
                {errors.title && <span className="field-error">{errors.title}</span>}
            </label>

            <label>
                Description
                <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3} />
            </label>

            <div className="form-row">
                <label>
                    Priority
                    <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
                        {PRIORITIES.map((p) => (
                            <option key={p.value} value={p.value}>
                                {p.label}
                            </option>
                        ))}
                    </select>
                </label>
                <label>
                    Status
                    <select value={status} onChange={(e) => setStatus(e.target.value as Status)}>
                        {STATUSES.map((s) => (
                            <option key={s.value} value={s.value}>
                                {s.label}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            <div className="form-row">
                <label>
                    Due date
                    <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
                    {errors.dueDate && <span className="field-error">{errors.dueDate}</span>}
                </label>
                <label>
                    Assignee email (optional)
                    <input
                        type="email"
                        value={assigneeEmail}
                        onChange={(e) => setAssigneeEmail(e.target.value)}
                        placeholder="demo@taskflow.com"
                    />
                </label>
            </div>

            <div className="row">
                <button className="btn btn-primary" type="submit" disabled={submitting}>
                    {submitting ? 'Saving...' : initial ? 'Save changes' : 'Create task'}
                </button>
                <button className="btn" type="button" onClick={onCancel}>
                    Cancel
                </button>
            </div>
        </form>
    )
}