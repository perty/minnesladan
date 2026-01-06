#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="minnesladan-local-db"
# Use image with pgvector pre-installed
IMAGE="ankane/pgvector:latest"
HOST_PORT=5432
CONTAINER_PORT=5432

if docker ps -a --format '{{.Names}}' | grep -wq "${CONTAINER_NAME}"; then
  EXISTING_IMAGE=$(docker inspect --format '{{.Config.Image}}' "${CONTAINER_NAME}")
  if [ "${EXISTING_IMAGE}" != "${IMAGE}" ]; then
    echo "Container '${CONTAINER_NAME}' already exists with image '${EXISTING_IMAGE}'."
    echo "Stop & remove it if you want to recreate with pgvector: docker rm -f ${CONTAINER_NAME}"
    exit 1
  fi
  echo "Starting existing container '${CONTAINER_NAME}'..."
  docker start "${CONTAINER_NAME}"
  exit 0
fi

echo "Running new Postgres container '${CONTAINER_NAME}'..."
docker run -d \
  --name "${CONTAINER_NAME}" \
  -e POSTGRES_USER=minnesladan \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=minnesladan \
  -p "${HOST_PORT}:${CONTAINER_PORT}" \
  "${IMAGE}"
