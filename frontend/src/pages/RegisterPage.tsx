import { useState, type FormEvent } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage } from '../api/client'

type Errors = { name?: string; email?: string; password?: string }

export default function RegisterPage() {
    const { register, isAuthenticated } = useAuth()
    const navigate = useNavigate()
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [errors, setErrors] = useState<Errors>({})
    const [serverError, setServerError] = useState('')
    const [loading, setLoading] = useState(false)

    if (isAuthenticated) return <Navigate to="/projects" replace />

    const validate = () => {
        const next: Errors = {}
        if (!name.trim()) next.name = 'Name is required'
        if (!email.trim()) next.email = 'Email is required'
        else if (!/^\S+@\S+\.\S+$/.test(email)) next.email = 'Email is not valid'
        if (password.length < 6) next.password = 'Password must be at least 6 characters'
        setErrors(next)
        return Object.keys(next).length === 0
    }

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setServerError('')
        if (!validate()) return
        setLoading(true)
        try {
            await register(name.trim(), email.trim(), password)
            navigate('/projects', { replace: true })
        } catch (err) {
            setServerError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-page">
            <form className="card auth-card" onSubmit={onSubmit} noValidate>
                <h1>Create account</h1>
                <p className="muted">Start tracking tasks with TaskFlow</p>

                {serverError && <div className="alert alert-error">{serverError}</div>}

                <label>
                    Name
                    <input value={name} onChange={(e) => setName(e.target.value)} />
                    {errors.name && <span className="field-error">{errors.name}</span>}
                </label>

                <label>
                    Email
                    <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" />
                    {errors.email && <span className="field-error">{errors.email}</span>}
                </label>

                <label>
                    Password
                    <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" />
                    {errors.password && <span className="field-error">{errors.password}</span>}
                </label>

                <button className="btn btn-primary" type="submit" disabled={loading}>
                    {loading ? 'Creating...' : 'Register'}
                </button>

                <p className="muted">
                    Already have an account? <Link to="/login">Log in</Link>
                </p>
            </form>
        </div>
    )
}