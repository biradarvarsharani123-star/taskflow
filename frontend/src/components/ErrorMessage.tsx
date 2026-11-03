export default function ErrorMessage({
                                         message,
                                         onRetry,
                                     }: {
    message: string
    onRetry?: () => void
}) {
    return (
        <div className="state-box">
            <div className="alert alert-error">{message}</div>
            {onRetry && (
                <button className="btn" onClick={onRetry}>
                    Try again
                </button>
            )}
        </div>
    )
}