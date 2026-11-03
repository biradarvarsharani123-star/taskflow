import { useAuth } from '../context/AuthContext'

export default function ProjectsPage() {
    const { user, logout } = useAuth()
    return (
        <div className="container">
            <h1>Projects</h1>
            <p>Logged in as {user?.name}</p>
            <button className="btn" onClick={logout}>Log out</button>
        </div>
    )
}