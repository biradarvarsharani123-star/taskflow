import type { Summary } from '../types'

export default function SummaryBar({ summary }: { summary: Summary }) {
    const items = [
        { label: 'To Do', value: summary.todo, className: '' },
        { label: 'In Progress', value: summary.inProgress, className: '' },
        { label: 'Done', value: summary.done, className: '' },
        { label: 'Overdue', value: summary.overdue, className: summary.overdue > 0 ? 'summary-overdue' : '' },
    ]
    return (
        <div className="summary-bar">
            {items.map((item) => (
                <div key={item.label} className={`summary-item ${item.className}`}>
                    <strong>{item.value}</strong>
                    <span>{item.label}</span>
                </div>
            ))}
        </div>
    )
}