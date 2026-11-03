import { STATUSES } from '../constants'
import type { Status, Task } from '../types'

export default function TaskCard({
                                     task,
                                     onStatusChange,
                                     onEdit,
                                     onDelete,
                                     disabled,
                                 }: {
    task: Task
    onStatusChange: (status: Status) => void
    onEdit: () => void
    onDelete: () => void
    disabled?: boolean
}) {
    return (
        <div className={`task-card${task.overdue ? ' overdue' : ''}`}>
            <div className="task-top">
                <strong>{task.title}</strong>
                <span className={`badge badge-${task.priority.toLowerCase()}`}>{task.priority}</span>
            </div>

            {task.description && <p className="muted task-desc">{task.description}</p>}

            <div className="task-meta">
                {task.dueDate && (
                    <span className={task.overdue ? 'due due-overdue' : 'due'}>
            Due {task.dueDate}
                        {task.overdue ? ' (overdue)' : ''}
          </span>
                )}
                {task.assigneeName && <span className="muted">{task.assigneeName}</span>}
            </div>

            <select
                value={task.status}
                onChange={(e) => onStatusChange(e.target.value as Status)}
                disabled={disabled}
                aria-label="Change status"
            >
                {STATUSES.map((s) => (
                    <option key={s.value} value={s.value}>
                        {s.label}
                    </option>
                ))}
            </select>

            <div className="task-actions">
                <button className="btn btn-small" onClick={onEdit}>
                    Edit
                </button>
                <button className="btn btn-small btn-danger" onClick={onDelete}>
                    Delete
                </button>
            </div>
        </div>
    )
}