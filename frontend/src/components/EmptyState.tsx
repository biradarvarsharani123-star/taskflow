export default function EmptyState({ title, hint }: { title: string; hint?: string }) {
    return (
        <div className="state-box">
            <strong>{title}</strong>
            {hint && <span className="muted">{hint}</span>}
        </div>
    )
}