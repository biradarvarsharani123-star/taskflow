import type { ReactNode } from 'react'

export default function Modal({
                                  title,
                                  onClose,
                                  children,
                              }: {
    title: string
    onClose: () => void
    children: ReactNode
}) {
    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal card" onClick={(e) => e.stopPropagation()} role="dialog" aria-label={title}>
                <div className="modal-header">
                    <h2>{title}</h2>
                    <button className="btn" onClick={onClose} aria-label="Close">
                        ✕
                    </button>
                </div>
                {children}
            </div>
        </div>
    )
}