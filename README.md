# TaskFlow

A small team task tracker: a **Spring Boot + Kotlin** REST API and a **React (TypeScript)** frontend.

## Tech Stack
- **Backend:** Spring Boot 3.5, Kotlin, Gradle, Spring Security (JWT), Spring Data JPA
- **Database:** H2 in-memory (data resets on restart; demo data is re-seeded on every start)
- **Frontend:** React, TypeScript, Vite, React Query, Axios, React Router
- **Testing:** JUnit 5 + MockK (backend), Vitest + React Testing Library (frontend)

## Prerequisites
- Java 17
- Node.js 18+

## Setup

### 1. Backend
```bash
cd backend
./gradlew bootRun        # Windows PowerShell: .\gradlew.bat bootRun
```
Runs on http://localhost:8080. Wait for `Started BackendApplicationKt`.

### 2. Frontend
```bash
cd frontend
npm install
npm run dev
```
Runs on http://localhost:5173. Vite proxies `/api` to the backend on port 8080.

### 3. Demo login
- **Email:** `demo@taskflow.com`
- **Password:** `Demo@123`

> Demo credentials are for local development only.

## Running Tests
```bash
# Backend
cd backend
./gradlew test

# Frontend
cd frontend
npx vitest run
```

## Architecture

### Backend (layered)
`Controller -> Service -> Repository`
- Controllers handle HTTP and validation.
- Services contain business logic and ownership checks (users only access their own projects).
- Repositories are Spring Data JPA interfaces.
- DTOs are used for requests and responses; JPA entities are never exposed.
- `@RestControllerAdvice` returns one consistent error shape with field-level messages.
- JWT auth through a Spring Security filter. All endpoints except register and login need a token.

### Frontend
- Separate API layer: one Axios instance with interceptors (attaches token, handles 401).
- `AuthContext` holds the session; protected routes redirect logged-out users to login.
- Reusable components: `TaskCard`, `Modal`, `Button`, `TaskForm`.
- Project board: three columns, summary bar (counts and overdue), filter by priority and assignee, sort by due date, overdue highlight.

## Design Patterns Used
- **Repository:** Spring Data repositories abstract data access.
- **Mapper:** entity to DTO conversion.
- **DTO:** separate request/response objects from entities.

## State Management: why React Query
React Query handles server state (caching, loading and error states, refetch after changes) with very little code. Auth state is small, so React Context is enough there.

## Assumptions
- A user can only see and modify projects they own.
- A task is overdue when its due date is before today and its status is not DONE.

## Trade-offs
- H2 in-memory keeps setup to one command, but data resets on restart. A seeder re-creates the demo user and sample project.
- Status changes use a dropdown instead of drag-and-drop to keep scope manageable.

## What I'd Do Next
- Docker Compose, Swagger/OpenAPI, Flyway migrations
- GitHub Actions for build and tests
- Activity log per task
- More tests (edge cases, end-to-end)
