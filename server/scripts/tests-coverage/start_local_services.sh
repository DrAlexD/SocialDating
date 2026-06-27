#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../log.sh"

command -v java >/dev/null 2>&1 || export PATH="$(cygpath -u "$JAVA_HOME")/bin:$PATH"
command -v java >/dev/null 2>&1 || { error "Java not found. Set JAVA_HOME."; exit 1; }

LOG_DIR="tests-coverage/logs"
mkdir -p "$LOG_DIR"

for name in gateway-service users-service categories-service defining-themes-service statements-service; do
  info "Starting $name..."
  java -javaagent:tests-coverage/kover-jvm-agent.jar=file:tests-coverage/agent.args \
    -jar "$name/build/libs/$name.jar" --spring.profiles.active=dev,local \
    >"$LOG_DIR/$name.log" 2>&1 &
done
