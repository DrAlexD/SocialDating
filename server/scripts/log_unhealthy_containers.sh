#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/log.sh"

section "Unhealthy containers"
docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead"

UNHEALTHY_CONTAINERS=$(docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead" --format "{{.Names}}")

for container in $UNHEALTHY_CONTAINERS; do
  section "Logs: $container"
  docker logs "$container" --tail=100
done
