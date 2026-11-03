import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import TaskForm from './TaskForm'

describe('TaskForm', () => {
    it('shows a validation error and does not submit when the title is empty', async () => {
        const onSubmit = vi.fn()
        render(<TaskForm submitting={false} onSubmit={onSubmit} onCancel={vi.fn()} />)

        await userEvent.click(screen.getByRole('button', { name: 'Create task' }))

        expect(screen.getByText('Title is required')).toBeInTheDocument()
        expect(onSubmit).not.toHaveBeenCalled()
    })

    it('submits the entered title', async () => {
        const onSubmit = vi.fn()
        render(<TaskForm submitting={false} onSubmit={onSubmit} onCancel={vi.fn()} />)

        await userEvent.type(screen.getByLabelText('Title'), 'Write docs')
        await userEvent.click(screen.getByRole('button', { name: 'Create task' }))

        expect(onSubmit).toHaveBeenCalledWith(expect.objectContaining({ title: 'Write docs' }))
    })
})