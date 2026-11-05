#!/bin/bash

# CPU 측정 옵션
WITH_CPU=false
ARGS=()
for arg in "$@"; do
  if [[ "$arg" == "--dbcpu" ]]; then
    WITH_CPU=true
  else
    ARGS+=("$arg")
  fi
done

OUT="${ARGS[0]:-results/monitor.csv}"

mkdir -p "$(dirname "$OUT")"
if [[ "$WITH_CPU" == true ]]; then
  echo "time,elapsed_s,hikari_active,hikari_pending,pg_lock_waiting,pg_active,db_cpu_pct" > "$OUT"
else
  echo "time,elapsed_s,hikari_active,hikari_pending,pg_lock_waiting,pg_active" > "$OUT"
fi

START=$(date +%s)
metric() {
  curl -s "http://localhost:8080/actuator/metrics/hikaricp.connections.$1" \
    | grep -o '"value":[0-9.]*' | head -n1 | cut -d: -f2
}
pg() {
  docker compose -f loadtest/docker-compose.yml exec -T postgres \
    psql -U loadtest -d loadtest -tAc "$1"
}
dbcpu() {
  docker stats --no-stream --format '{{.CPUPerc}}' \
    "$(docker compose -f loadtest/docker-compose.yml ps -q postgres)" | tr -d '%'
}

while true; do
  NOW=$(( $(date +%s) - START ))
  ACTIVE=$(metric active)
  PENDING=$(metric pending)
  LOCKWAIT=$(pg "SELECT count(*) FROM pg_stat_activity WHERE wait_event_type='Lock';")
  PGACTIVE=$(pg "SELECT count(*) FROM pg_stat_activity WHERE state='active';")

  if [[ "$WITH_CPU" == true ]]; then
    CPU=$(dbcpu)
    echo "$(date +%T),$NOW,$ACTIVE,$PENDING,$LOCKWAIT,$PGACTIVE,$CPU" >> "$OUT"
  else
    echo "$(date +%T),$NOW,$ACTIVE,$PENDING,$LOCKWAIT,$PGACTIVE" >> "$OUT"
  fi

  sleep 1
done