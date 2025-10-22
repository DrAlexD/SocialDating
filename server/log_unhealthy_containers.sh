#!/bin/bash

echo ""
echo ""
echo ""
docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead"

UNHEALTHY_CONTAINERS=$(docker ps -a --filter "health=unhealthy" --filter "status=exited" --filter "status=dead" --format "{{.Names}}")

for container in $UNHEALTHY_CONTAINERS; do
    echo ""
    echo ""
    echo ""
    echo "================================================================ $container ================================================================"
    docker logs "$container" --tail=100
done