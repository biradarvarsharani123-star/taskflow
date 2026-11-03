import axios from 'axios'

const TOKEN_KEY = 'taskflow_token'
const USER_KEY = 'taskflow_user'

export const tokenStorage = {
    get: () => localStorage.getItem(TOKEN_KEY),
    set: (token: string) => localStorage.setItem(TOKEN_KEY, token),
    clear: () => {
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USER_KEY)
    },
}

export const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
    const token = tokenStorage.get()
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const isAuthCall = error.config?.url?.startsWith('/auth/')
        if (error.response?.status === 401 && !isAuthCall) {
            tokenStorage.clear()
            window.location.href = '/login?expired=1'
        }
        return Promise.reject(error)
    },
)

export function getErrorMessage(error: unknown): string {
    if (axios.isAxiosError(error)) {
        return error.response?.data?.message ?? error.message
    }
    return 'Something went wrong'
}