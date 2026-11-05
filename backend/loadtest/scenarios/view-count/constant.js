import http from 'k6/http';

const RATE = Number(__ENV.RATE || 1000);
const DURATION = __ENV.DURATION || '10s';

export const options = {
    scenarios: {
        constant: {
            executor: 'constant-arrival-rate',
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 3000,
            rate: RATE,
            duration: DURATION
        }
    },
    thresholds: {
        http_req_duration: ['p(99)<500'],
        http_req_failed: ['rate<0.01'],
        dropped_iterations: ['count==0']
    },
    summaryTrendStats: ['avg', 'max', 'med', 'p(95)', 'p(99)']
};

const URL = 'http://localhost:8080/api/posts/1';

export default function() {
    http.cookieJar().clear(URL);
    http.get(URL);
}