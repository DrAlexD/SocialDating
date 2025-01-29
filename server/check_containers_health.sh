#!/bin/bash

INTERVAL=5

while true; do
  TOTAL_COUNT=$(docker ps -a --format "{{.Names}}" | wc -l)
  HEALTHY_COUNT=$(docker ps -a --filter "health=healthy" --format "{{.Names}}" | wc -l)
  UNHEALTHY_COUNT=$(docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead" --format "{{.Names}}" | wc -l)

  if [ "$UNHEALTHY_COUNT" -gt 0 ]; then
    echo "Error: At least one service has failed! ($UNHEALTHY_COUNT unhealthy/exited/dead)"
    docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead"
    exit 1
  fi

  if [ "$HEALTHY_COUNT" -eq "$TOTAL_COUNT" ]; then
    echo "All services are healthy!"
    exit 0
  else
    echo "Waiting for services to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)..."
  fi

  sleep "$INTERVAL"
done
