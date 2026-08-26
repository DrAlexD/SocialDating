#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../log.sh"

INTERVAL=5
TIMEOUT=600
PORTS="8080 8010 8020 8030 8040"

for PORT in $PORTS; do
  info "Sending shutdown to service on port $PORT..."
  curl -s --max-time 2 -X POST "http://localhost:$PORT/actuator/shutdown" >/dev/null
done

ELAPSED=0

while true; do
  RUNNING_COUNT=0
  TOTAL_COUNT=0

  for PORT in $PORTS; do
    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    if curl -fs "http://localhost:$PORT/actuator/health" >/dev/null 2>&1; then
      RUNNING_COUNT=$((RUNNING_COUNT + 1))
    fi
  done

  if [ "$ELAPSED" -ge "$TIMEOUT" ]; then
    error "Timed out after ${TIMEOUT}s waiting for services to stop ($RUNNING_COUNT/$TOTAL_COUNT still running)."
    exit 1
  fi

  if [ "$RUNNING_COUNT" -eq 0 ]; then
    info "All services stopped."
    exit 0
  fi

  info "Waiting for services to stop ($RUNNING_COUNT/$TOTAL_COUNT still running)... (${ELAPSED}s/${TIMEOUT}s)."
  sleep "$INTERVAL"
  ELAPSED=$((ELAPSED + INTERVAL))
done
