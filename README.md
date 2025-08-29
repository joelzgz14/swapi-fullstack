# SWAPI Project

Este repositorio contiene una aplicación **full-stack** para explorar datos de *Star Wars* utilizando la [SWAPI (Star Wars API)](https://swapi.dev).

---

## Features
-  Explorar planetas y personajes de Star Wars
-  Funcionalidad de búsqueda
- ️ Ordenación y paginación
-  Diseño responsive

---

## Tecnologías utilizadas
- **Frontend**: Vue 3
- **Backend**: Java
---

##  Ejecución de la aplicación

Clona este repositorio:

```bash
git clone https://github.com/joelzgz14/swapi-fullstack.git
cd swapi-project-full

Construye e inicia los contenedores:

docker compose up --build

💡 La primera construcción puede tardar algunos minutos.
Una vez iniciado, podrás acceder a:

# Backend disponible en http://localhost:6969/api/people
# Frontend disponible en http://localhost:5173

---

📡 API Endpoints

El backend proporciona los siguientes endpoints:

GET /planets → Lista todos los planetas con paginación y ordenación

GET /people → Lista todos los personajes con paginación y ordenación

Backend: endpoints
GET /planets?page=1&size=15&search=&sort=name|created&dir=asc|desc
GET /people?page=1&size=15&search=&sort=name|created&dir=asc|desc
GET /health

🔎 Parámetros de consulta

page → Número de página (default: 1)

size → Elementos por página (default: 15)

search → Término de búsqueda

sort → Campo de ordenación (default: name)

dir → Dirección de ordenación (asc/desc, default: desc)

---

## Estructura del proyecto
frontend/           # Aplicación Vue.js
backend/            # Aplicación Spring Boot
docker-compose.yml  # Configuración de Docker Compose

---
## Troubleshooting

Si encuentras conflictos de puertos, modifica el mapeo en docker-compose.yml.

Logs del backend:

docker-compose logs backend

