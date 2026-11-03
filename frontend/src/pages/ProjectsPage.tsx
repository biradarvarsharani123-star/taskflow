import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../context/AuthContext'
import { createProject, getProjects } from '../api/projects'
import { getErrorMessage } from '../api/client'
import Spinner from '../components/Spinner'
import ErrorMessage from '../components/ErrorMessage'
import EmptyState from '../components/EmptyState'
import Modal from '../components/Modal'

export default function ProjectsPage() {
    const { user, logout } = useAuth()
    const queryClient = useQueryClient()
    const [open, setOpen] = useState(false)
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [nameError, setNameError] = useState('')

    const projects = useQuery({ queryKey: ['projects'], queryFn: getProjects })

    const create = useMutation({
        mutationFn: createProject,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['projects'] })
            setOpen(false)
            setName('')
            setDescription('')
        },
    })

    const onSubmit = (e: FormEvent) => {
        e.preventDefault()
        if (!name.trim()) {
            setNameError('Project name is required')
            return
        }
        setNameError('')
        create.mutate({ name: name.trim(), description: description.trim() || undefined })
    }

    return (
        <div className="container">
            <header className="topbar">
                <div>
                    <h1>Projects</h1>
                    <p className="muted">Logged in as {user?.name}</p>
                </div>
                <div className="row">
                    <button className="btn btn-primary" onClick={() => setOpen(true)}>
                        + New project
                    </button>
                    <button className="btn" onClick={logout}>
                        Log out
                    </button>
                </div>
            </header>

            {projects.isLoading && <Spinner label="Loading projects..." />}
            {projects.isError && (
                <ErrorMessage message={getErrorMessage(projects.error)} onRetry={() => projects.refetch()} />
            )}
            {projects.data && projects.data.length === 0 && (
                <EmptyState title="No projects yet" hint="Create your first project to get started." />
            )}

            <div className="grid">
                {projects.data?.map((p) => (
                    <Link key={p.id} to={`/projects/${p.id}`} className="card project-card">
                        <h3>{p.name}</h3>
                        <p className="muted">{p.description || 'No description'}</p>
                        <small className="muted">Created {new Date(p.createdAt).toLocaleDateString()}</small>
                    </Link>
                ))}
            </div>

            {open && (
                <Modal title="New project" onClose={() => setOpen(false)}>
                    <form className="form" onSubmit={onSubmit} noValidate>
                        {create.isError && <div className="alert alert-error">{getErrorMessage(create.error)}</div>}
                        <label>
                            Name
                            <input value={name} onChange={(e) => setName(e.target.value)} maxLength={120} />
                            {nameError && <span className="field-error">{nameError}</span>}
                        </label>
                        <label>
                            Description
                            <textarea
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                rows={3}
                            />
                        </label>
                        <button className="btn btn-primary" type="submit" disabled={create.isPending}>
                            {create.isPending ? 'Creating...' : 'Create project'}
                        </button>
                    </form>
                </Modal>
            )}
        </div>
    )
}