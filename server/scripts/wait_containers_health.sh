#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/log.sh"

INTERVAL=5
TIMEOUT=600

ELAPSED=0

while true; do
  TOTAL_COUNT=$(docker ps -a --format "{{.Names}}" | wc -l)
  HEALTHY_COUNT=$(docker ps -a --filter "health=healthy" --format "{{.Names}}" | wc -l)
  UNHEALTHY_COUNT=$(docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead" --format "{{.Names}}" | wc -l)

  if [ "$UNHEALTHY_COUNT" -gt 0 ]; then
    error "At least one container has failed ($UNHEALTHY_COUNT unhealthy/exited/dead)."
    exit 1
  fi

  if [ "$ELAPSED" -ge "$TIMEOUT" ]; then
    error "Timed out after ${TIMEOUT}s waiting for containers to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)."
    exit 1
  fi

  if [ "$HEALTHY_COUNT" -eq "$TOTAL_COUNT" ]; then
    info "All containers are healthy."
    exit 0
  fi

  info "Waiting for containers to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)... (${ELAPSED}s/${TIMEOUT}s)."
  sleep "$INTERVAL"
  ELAPSED=$((ELAPSED + INTERVAL))
done
