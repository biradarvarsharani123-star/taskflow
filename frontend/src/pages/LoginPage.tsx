import { useState, type FormEvent } from 'react'
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage } from '../api/client'

export default function LoginPage() {
    const { login, isAuthenticated } = useAuth()
    const navigate = useNavigate()
    const [params] = useSearchParams()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [errors, setErrors] = useState<{ email?: string; password?: string }>({})
    const [serverError, setServerError] = useState('')
    const [loading, setLoading] = useState(false)

    if (isAuthenticated) return <Navigate to="/projects" replace />

    const validate = () => {
        const next: { email?: string; password?: string } = {}
        if (!email.trim()) next.email = 'Email is required'
        else if (!/^\S+@\S+\.\S+$/.test(email)) next.email = 'Email is not valid'
        if (!password) next.password = 'Password is required'
        setErrors(next)
        return Object.keys(next).length === 0
    }

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setServerError('')
        if (!validate()) return
        setLoading(true)
        try {
            await login(email.trim(), password)
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
                <h1>Welcome back</h1>
                <p className="muted">Log in to TaskFlow</p>

                {params.get('expired') && (
                    <div className="alert alert-warn">Your session expired. Please log in again.</div>
                )}
                {serverError && <div className="alert alert-error">{serverError}</div>}

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
                    {loading ? 'Logging in...' : 'Log in'}
                </button>

                <p className="muted">
                    No account? <Link to="/register">Register</Link>
                </p>
            </form>
        </div>
    )
}