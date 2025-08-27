# SWAPI Project (monorepo for the challenge)

Estructura principal:
- /backend -> microservicio Spring Boot (WebFlux)
- /frontend-placeholder -> carpeta donde irá el frontend Angular

## Backend: endpoints
- GET /api/people?page=1&size=15&search=&sort=name|created&dir=asc|desc
- GET /api/planets?page=1&size=15&search=&sort=name|created&dir=asc|desc
- GET /api/health

## Run with Docker Compose
```bash
docker compose up --build
# Backend available at http://localhost:6969/api/people
```

## Siguientes pasos
- Implementar frontend Angular dentro de /frontend (yo puedo generarlo y conectarlo a estos endpoints).
- Añadir caching y tests de integración para más robustez.
