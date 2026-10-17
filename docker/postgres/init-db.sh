#!/bin/bash
set -euo pipefail
for svc in fleet delivery tracking notification analytics keycloak ai; do
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -c "CREATE USER ${svc}_user WITH PASSWORD '${DB_PASSWORD:-local}';"
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -c "CREATE DATABASE ${svc}_db OWNER ${svc}_user;"
done
psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d ai_db -c "CREATE EXTENSION IF NOT EXISTS vector;"
