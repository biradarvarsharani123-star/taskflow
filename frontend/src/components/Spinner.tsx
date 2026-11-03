export default function Spinner({ label = 'Loading...' }: { label?: string }) {
    return (
        <div className="state-box" role="status">
            <div className="spinner" />
            <span className="muted">{label}</span>
        </div>
    )
}