#!/bin/bash

info()    { echo "[$(date +%H:%M:%S)] [INFO] $*"; }
warn()    { echo "[$(date +%H:%M:%S)] [WARN] $*" >&2; }
error()   { echo "[$(date +%H:%M:%S)] [ERROR] $*" >&2; }
section() {
  echo ""
  echo "[$(date +%H:%M:%S)] ==================== $* ===================="
}
