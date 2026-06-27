#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../log.sh"

INTERVAL=5
TIMEOUT=600
PORTS="8080 8010 8020 8030 8040"

ELAPSED=0

while true; do
  TOTAL_COUNT=0
  HEALTHY_COUNT=0
  UNHEALTHY_COUNT=0

  for PORT in $PORTS; do
    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 2 "http://localhost:$PORT/actuator/health")

    if [ "$CODE" = "200" ]; then
      HEALTHY_COUNT=$((HEALTHY_COUNT + 1))
    elif [ "$CODE" != "000" ]; then
      UNHEALTHY_COUNT=$((UNHEALTHY_COUNT + 1))
    fi
  done

  if [ "$UNHEALTHY_COUNT" -gt 0 ]; then
    error "At least one service has failed ($UNHEALTHY_COUNT returned a non-200/non-000 code)."
    exit 1
  fi

  if [ "$ELAPSED" -ge "$TIMEOUT" ]; then
    error "Timed out after ${TIMEOUT}s waiting for services to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)."
    exit 1
  fi

  if [ "$HEALTHY_COUNT" -eq "$TOTAL_COUNT" ]; then
    info "All services are healthy."
    exit 0
  fi

  info "Waiting for services to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)... (${ELAPSED}s/${TIMEOUT}s)."
  sleep "$INTERVAL"
  ELAPSED=$((ELAPSED + INTERVAL))
done
