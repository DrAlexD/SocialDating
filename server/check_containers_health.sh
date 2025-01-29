#!/bin/bash

MAX_WAIT_TIME=300
INTERVAL=5

ELAPSED_TIME=0

while [ "$ELAPSED_TIME" -lt "$MAX_WAIT_TIME" ]; do
  HEALTHY_COUNT=$(docker ps --filter "health=healthy" --format "{{.Names}}" | wc -l)
  TOTAL_COUNT=$(docker ps --format "{{.Names}}" | wc -l)

  if [ "$HEALTHY_COUNT" -eq "$TOTAL_COUNT" ]; then
    echo "All services are healthy!"
    exit 0
  else
    echo "Waiting for services to become healthy ($HEALTHY_COUNT/$TOTAL_COUNT)..."
  fi

  ELAPSED_TIME=$((ELAPSED_TIME + INTERVAL))
  sleep INTERVAL
done

echo "Timeout reached: Services are not healthy after $MAX_WAIT_TIME seconds."
exit 1
