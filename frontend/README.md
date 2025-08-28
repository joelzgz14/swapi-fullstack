# SWAPI Vue Frontend

## Dev
npm install
npm run dev
# http://localhost:5173
# Env: VITE_API_BASE=http://localhost:6969/api

## Docker
docker build -t swapi-frontend .
docker run -p 5173:80 swapi-frontend
