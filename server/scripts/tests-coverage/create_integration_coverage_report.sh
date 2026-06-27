#!/bin/bash

set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/../.."
source ./scripts/log.sh

services_started=false
services_stopped=false

cleanup() {
  rc=$?
  if [ "$services_started" = true ] && [ "$services_stopped" = false ]; then
    section "Stop local services (cleanup)"
    ./scripts/tests-coverage/stop_local_services.sh || true
  fi
  if [ "$rc" -ne 0 ]; then
    error "FAILED! Exit code $rc."
  fi
  exit "$rc"
}
trap cleanup EXIT

section "1/9 Build JARs"
./gradlew build -x test

section "2/9 Start infrastructure containers"
docker compose up -d zookeeper kafka postgres

section "3/9 Wait for infrastructure containers health"
./scripts/wait_containers_health.sh

section "4/9 Start local services"
services_started=true
./scripts/tests-coverage/start_local_services.sh

section "5/9 Wait for local services health"
./scripts/tests-coverage/wait_local_services_health.sh

section "6/9 Run global integration tests"
./gradlew integrationTest

section "7/9 Stop local services"
./scripts/tests-coverage/stop_local_services.sh
services_stopped=true

section "8/9 Build reports"
./scripts/tests-coverage/build_reports.sh

section "9/9 Remove report data"
./scripts/tests-coverage/remove_report_data.sh

info "SUCCESS! Reports are written to tests-coverage/reports/integration."
