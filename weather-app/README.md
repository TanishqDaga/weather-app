# Basic Weather App

React (Vite) frontend + Spring Boot (Java 17) backend. Login page first, then city weather via the free Open-Meteo API (no API key).

## Run

Backend (needs Java 17 and Maven), from `backend/`:

    mvn spring-boot:run

Runs on http://localhost:8080

Frontend (needs Node 18+), from `frontend/`:

    npm install
    npm run dev

Open http://localhost:5173 and sign in with **demo / demo123**.

## How it works

- `POST /api/login` checks the hard-coded demo user and returns a random token (stored in memory).
- `GET /api/weather?city=...` requires `Authorization: Bearer <token>`, geocodes the city, then fetches current weather.
- Vite proxies `/api` to the backend, so no CORS config is needed in development.
