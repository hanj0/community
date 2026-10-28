import http from 'k6/http';
import { check } from 'k6';

export const options = { vus: 5, iterations: 1000 };

const URL = 'http://localhost:8080/api/posts/1';

export default function () {
  http.cookieJar().clear(URL);          // 매 요청마다 쿠키 제거 → 항상 증가 경로
  const res = http.get(URL);
  check(res, { 'status 200': (r) => r.status === 200 });
}