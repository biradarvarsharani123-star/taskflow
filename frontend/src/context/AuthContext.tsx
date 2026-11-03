
import {
    createContext,
    useContext,
    useMemo,
    useState,
    type ReactNode,
} from 'react'

import { tokenStorage } from '../api/client'
import * as authApi from '../api/auth'
import type { AuthResponse } from '../types'

interface User {
    name: string
    email: string
}

interface AuthContextValue {
    user: User | null
    isAuthenticated: boolean
    login: (email: string, password: string) => Promise<void>
    register: (name: string, email: string, password: string) => Promise<void>
    logout: () => void
}

const USER_KEY = 'taskflow_user'

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function loadUser(): User | null {
    try {
        const raw = localStorage.getItem(USER_KEY)

        return raw && tokenStorage.get()
            ? (JSON.parse(raw) as User)
            : null
    } catch {
        return null
    }
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(loadUser)

    const save = (res: AuthResponse) => {
        tokenStorage.set(res.token)

        const u = {
            name: res.name,
            email: res.email,
        }

        localStorage.setItem(USER_KEY, JSON.stringify(u))
        setUser(u)
    }

    const value = useMemo<AuthContextValue>(
        () => ({
            user,
            isAuthenticated: user !== null,

            login: async (email, password) => {
                const response = await authApi.login(email, password)
                save(response)
            },

            register: async (name, email, password) => {
                const response = await authApi.register(name, email, password)
                save(response)
            },

            logout: () => {
                tokenStorage.clear()
                localStorage.removeItem(USER_KEY)
                setUser(null)
            },
        }),
        [user],
    )

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext)

    if (!ctx) {
        throw new Error('useAuth must be used inside AuthProvider')
    }

    return ctx
}
