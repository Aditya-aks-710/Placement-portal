# Placement Portal

A full-stack web application for managing and showcasing student placement records, interview experiences, and career statistics. Students register with their registration number, submit placement offers (subject to admin approval), and share interview experiences. Admins review and approve submissions.

- **Backend:** Spring Boot 4 (Java 17) + MongoDB + Spring Security (JWT)
- **Frontend:** Vite + React 18 + TypeScript + Tailwind CSS + shadcn/ui + TanStack Query

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started (Clone & Run)](#getting-started-clone--run)
  - [1. Clone](#1-clone)
  - [2. Configure the Backend](#2-configure-the-backend)
  - [3. Run the Backend](#3-run-the-backend)
  - [4. Configure & Run the Frontend](#4-configure--run-the-frontend)
- [Environment Variables](#environment-variables)
- [Application Flow](#application-flow)
- [API Overview](#api-overview)
- [Testing](#testing)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)

---

## Tech Stack

| Layer      | Technologies |
|------------|--------------|
| Frontend   | React 18, TypeScript, Vite, Tailwind CSS, shadcn/ui (Radix UI), TanStack Query, React Router, Sonner |
| Backend    | Spring Boot 4.0.2, Spring Web MVC, Spring Data MongoDB, Spring Security, JJWT (JWT) |
| Database   | MongoDB (local or MongoDB Atlas) |
| Build Tools| Maven (backend, via wrapper), npm (frontend) |
| Testing    | JUnit / Spring Boot Test (backend), Vitest (frontend) |

---

## Project Structure

```
Placement-portal/
├── backend/                            # Spring Boot application
│   ├── mvnw / mvnw.cmd                 # Maven wrapper (no global Maven needed)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/nit/placement_portal/
│       │   │   ├── PlacementPortalApplication.java   # App entry point
│       │   │   ├── bootstrap/          # Startup seeders (e.g. AdminSeeder)
│       │   │   ├── config/             # SecurityConfig, WebConfig (CORS)
│       │   │   ├── controller/         # REST controllers
│       │   │   ├── dto/                # Request/response DTOs
│       │   │   ├── model/              # MongoDB document models
│       │   │   ├── repository/         # Spring Data Mongo repositories
│       │   │   ├── security/           # JWT filters & helpers
│       │   │   └── service/            # Business logic
│       │   └── resources/
│       │       └── application.properties
│       └── test/                       # Backend tests
│
├── frontend/                           # Vite + React app
│   ├── package.json
│   ├── vite.config.ts                  # Dev server + /api proxy to backend
│   ├── index.html
│   └── src/
│       ├── main.tsx / App.tsx          # Entry & routes
│       ├── components/                 # Shared components (+ ui/ for shadcn)
│       ├── pages/                      # Index, Login, Register, Stats, StudentDetail, Admin, NotFound
│       ├── lib/                        # api.ts (API client), auth.ts, utils.ts
│       ├── hooks/                      # Custom hooks
│       └── data/                       # Mock/seed data helpers
│
└── README.md
```

---

## Prerequisites

Make sure the following are installed:

- **Java 17+** (JDK 17 or newer; the project targets Java 17)
- **Node.js 18+** and **npm**
- **MongoDB** — either a local instance or a free [MongoDB Atlas](https://www.mongodb.com/atlas) cluster
- **Git**

> Maven is **not** required globally — the backend ships with the Maven wrapper (`mvnw` / `mvnw.cmd`).

---

## Getting Started (Clone & Run)

### 1. Clone

```bash
git clone <your-repo-url>
cd Placement-portal
```

### 2. Configure the Backend

Create a `.env` file inside `backend/` (it is git-ignored). Copy from the template:

```bash
# from the backend/ folder
cp .env.example .env        # macOS / Linux
copy .env.example .env      # Windows (cmd)
```

Then edit `backend/.env` and set at minimum your `MONGODB_URI` and a strong `APP_JWT_SECRET` (≥ 32 characters). See [Environment Variables](#environment-variables).

The backend loads this file via Spring's `spring.config.import=optional:file:.env[.properties]` — no extra dotenv library is needed.

### 3. Run the Backend

```bash
cd backend

# macOS / Linux
./mvnw spring-boot:run

# Windows (PowerShell / cmd)
.\mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8080**. On first startup, if `APP_BOOTSTRAP_ADMIN_ENABLED=true`, an admin account is created from the configured credentials.

### 4. Configure & Run the Frontend

In a **separate terminal**:

```bash
cd frontend

# Point the frontend at the backend (optional — defaults to http://localhost:8080)
cp .env.example .env.local    # macOS / Linux
copy .env.example .env.local  # Windows (cmd)

npm install
npm run dev
```

The app runs on **http://localhost:5173**. The Vite dev server proxies `/api` requests to the backend.

> On Windows PowerShell, if `npm` is blocked by the execution policy, use `npm.cmd run dev`.

---

## Environment Variables

### Backend (`backend/.env`)

| Variable | Required | Description |
|----------|----------|-------------|
| `MONGODB_URI` | Yes | MongoDB connection string. Local: `mongodb://localhost:27017/placement_portal`. Atlas: `mongodb+srv://<user>:<pass>@<cluster>/<db>?retryWrites=true&w=majority` |
| `APP_JWT_SECRET` | Yes | HS256 signing secret — **must be at least 32 characters** |
| `APP_JWT_EXPIRATION_MS` | No | Token lifetime in ms (default `3600000` = 1 hour) |
| `APP_BOOTSTRAP_ADMIN_ENABLED` | No | If `true`, seeds an admin user on startup |
| `APP_BOOTSTRAP_ADMIN_USERNAME` | No | Username for the seeded admin |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | No | Password for the seeded admin |
| `APP_REGISTRATION_EXPOSE_TOKEN` | No | Dev only: returns the activation token in the registration response so students can self-activate without email. Keep `false` in production |
| `APP_CORS_ALLOWED_ORIGINS` | No | Comma-separated list of allowed frontend origins |

### Frontend (`frontend/.env.local`)

| Variable | Required | Description |
|----------|----------|-------------|
| `VITE_API_BASE_URL` | No | Backend base URL (default `http://localhost:8080`) |

> **Never commit real secrets.** The `.env` files are git-ignored; only the `.env.example` templates are tracked.

---

## Application Flow

1. **Browse** — Anyone can view the student directory, individual profiles, and placement statistics.
2. **Register** — A student activates their account using their registration number (which must already exist in the database). In dev mode the activation token is returned directly; otherwise it would be emailed.
3. **Login** — Students log in with their registration number; admins log in with admin credentials. A JWT is issued and stored client-side.
4. **Submit** — Logged-in students submit placement offers (with a searchable, add-as-you-type company picker) and share interview experiences. Submissions start in a `PENDING` state.
5. **Approve** — Admins review pending placement requests and approve or reject them. Approved placements appear publicly on the student's profile.

---

## API Overview

Base URL: `http://localhost:8080`

| Area | Endpoint(s) | Notes |
|------|-------------|-------|
| Auth | `POST /api/auth/initiate-registration`, `POST /api/auth/complete-registration`, `POST /api/auth/login` | Registration & login |
| Students (public) | `GET /api/public/students`, `GET /api/public/students/page`, `GET /api/public/students/filters` | Directory, paging, filter options |
| Companies | `GET /api/companies` | Company suggestions |
| Placement requests | `POST /api/placement-requests` | Student submits an offer (auth required) |
| Admin | `GET /api/admin/placement-requests`, `PUT /api/admin/placement-requests/{id}/approve`, `PUT /api/admin/placement-requests/{id}/reject` | Admin-only review |
| Experiences | `POST /api/experiences`, `GET /api/experiences/my`, `GET /api/experiences/placement/{id}` | Interview experiences |

Protected endpoints require an `Authorization: Bearer <token>` header.

---

## Testing

**Backend:**

```bash
cd backend
./mvnw test        # or .\mvnw.cmd test on Windows
```

**Frontend:**

```bash
cd frontend
npm test           # run once
npm run test:watch # watch mode
```

---

## Contributing

Contributions are welcome! To contribute:

1. **Fork** the repository and **clone** your fork.
2. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes. Keep them focused and follow the existing code style.
4. Run linters and tests before committing:
   ```bash
   cd frontend && npm run lint && npm test
   cd ../backend && ./mvnw test
   ```
5. Commit with a clear, descriptive message:
   ```bash
   git commit -m "feat: add <short description>"
   ```
6. Push and open a **Pull Request** against the `main` branch, describing what changed and why.

**Guidelines:**
- Do not commit `.env` files or any secrets.
- Keep PRs small and scoped to a single concern where possible.
- Match the existing formatting and naming conventions.
- Add or update tests for behavior you change.

---

## Troubleshooting

- **Backend connects to `localhost:27017` instead of Atlas** — Spring Boot 4 uses `spring.mongodb.uri` (not the older `spring.data.mongodb.uri`). Ensure your `MONGODB_URI` is set in `backend/.env`.
- **No students appear** — Confirm `MONGODB_URI` points to the database that actually contains the `students` collection.
- **Login returns 401** — Verify the user exists and the password is correct; check the bootstrap admin variables if using the seeded admin.
- **`npm` blocked on Windows PowerShell** — Use `npm.cmd ...` instead of `npm ...`.
- **First request is slow** — The backend lazily initializes on the first HTTP request; subsequent requests are fast.
