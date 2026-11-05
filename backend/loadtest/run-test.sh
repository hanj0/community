#!/bin/bash
#   ./loadtest/run-test.sh <주제> <방식> constant <RPS> [초=60]
#   ./loadtest/run-test.sh <주제> <방식> ramp
set -u
MODE="${1:?방식(db|buffered)}"
SCENARIO="${2:?시나리오(constant|ramp)}"

# 주제에 따라 수정
TABLE="post"
REDIS_KEY="post:viewCount:1"
DC="docker compose -f loadtest/docker-compose.yml"
SC_DIR="loadtest/scenarios/view-count"

case "$SCENARIO" in
  constant)
    RATE="${3:?RPS}"
    DURATION="${4:-60}"
    OUT="results/${MODE}-c${RATE}-$(date +%m%d-%H%M)"
    K6_ARGS=(-e RATE="$RATE" -e DURATION="${DURATION}s")
    ;;
  ramp)
    OUT="results/${MODE}-ramp-$(date +%m%d-%H%M)"
    K6_ARGS=()
    ;;
  *) echo "시나리오는 constant 또는 ramp"; exit 1 ;;
esac
mkdir -p "$OUT"

if ss -ltn '( sport = :8080 )' | grep -q LISTEN; then
  echo "8080이 이미 사용 중입니다. 기존 앱을 먼저 종료하세요."; exit 1
fi

cleanup() {
  kill $MON_PID $CONN_PID 2>/dev/null
  kill -- -"$APP_PID" 2>/dev/null
}
trap cleanup EXIT

# 초기화 (Redis, 조회수, 죽은 행)
echo "[1/7]초기화 (Redis, 조회수, 죽은 행)"
[ "$MODE" = "buffered" ] && $DC exec -T redis redis-cli FLUSHDB >/dev/null
$DC exec -T postgres psql -U loadtest -d loadtest -c "UPDATE $TABLE SET view_count = 0 WHERE id = 1;" >/dev/null
$DC exec -T postgres psql -U loadtest -d loadtest -c "VACUUM (ANALYZE) $TABLE;" >/dev/null

# 앱 기동 (새 프로세스 그룹으로 백그라운드 실행)
echo "[2/7] 앱 기동 중"
setsid "$SC_DIR/run.sh" "$MODE" > "$OUT/app.log" 2>&1 &
APP_PID=$!
for i in $(seq 120); do
  curl -sf http://localhost:8080/actuator/metrics/hikaricp.connections.pending >/dev/null && break
  printf "\r      서버 대기 중... %ds" "$i"
  sleep 1
  [ "$i" = 120 ] && { echo "앱 기동 실패: $OUT/app.log 확인"; exit 1; }
done
echo

# 워밍업
echo "[3/7] 워밍업 (100 RPS 10초)"
k6 run -q -e RATE=100 -e DURATION=10s "$SC_DIR/constant.js" >/dev/null 2>&1
sleep 5

# 백그라운드 기록
echo "[4/7] 모니터링 시작"
./loadtest/monitor.sh "$OUT/monitor.csv" &
MON_PID=$!
( echo "time,server_estab,k6_conns"
  while true; do
    S=$(ss -tan state established '( sport = :8080 )' | tail -n +2 | wc -l)
    C=$(ss -tan state established '( dport = :8080 )' | tail -n +2 | wc -l)
    echo "$(date +%T),$S,$C"; sleep 2
  done ) > "$OUT/conns.csv" &
CONN_PID=$!

# 본 실행
echo "[5/7] k6 테스트 실행 ($SCENARIO)"
k6 run "${K6_ARGS[@]}" --summary-export="$OUT/summary.json" "$SC_DIR/${SCENARIO}.js"
sleep 3

# flush 대기
if [ "$MODE" = "buffered" ]; then
  echo "[6/7] write-back flush 대기 (최대 30초)"
  for i in $(seq 30); do
    V=$($DC exec -T redis redis-cli GET "$REDIS_KEY" | tr -d '\r')
    if [ -z "$V" ] || [ "$V" = "0" ]; then break; fi
    printf "\r      대기 중... %ds (Redis 값: %s)" "$i" "$V"; sleep 1
  done
  echo
fi

# 정합성 확인 출력
echo "[7/7] 정합성: DB view_count = $($DC exec -T postgres psql -U loadtest -d loadtest -tAc "SELECT view_count FROM $TABLE WHERE id=1;")"

echo "완료: $OUT/"