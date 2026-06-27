#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../log.sh"

command -v java >/dev/null 2>&1 || export PATH="$(cygpath -u "$JAVA_HOME")/bin:$PATH"
command -v java >/dev/null 2>&1 || { error "Java not found. Set JAVA_HOME."; exit 1; }

java -jar tests-coverage/kover-cli.jar report tests-coverage/reports/integration/report.ic \
  --classfiles common/build/classes/kotlin/main --classfiles gateway-service/build/classes/kotlin/main \
  --classfiles users-service/build/classes/kotlin/main --classfiles categories-service/build/classes/kotlin/main \
  --classfiles defining-themes-service/build/classes/kotlin/main --classfiles statements-service/build/classes/kotlin/main \
  --src common/src/main/kotlin --src gateway-service/src/main/kotlin --src users-service/src/main/kotlin \
  --src categories-service/src/main/kotlin --src defining-themes-service/src/main/kotlin --src statements-service/src/main/kotlin \
  --exclude "*ServiceApplicationKt" \
  --html tests-coverage/reports/integration --xml tests-coverage/reports/integration/report.xml
