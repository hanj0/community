import http from 'k6/http';

export const options = {
    scenarios: {
        ramp: {
            executor: 'ramping-arrival-rate',
            startRate: 100,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 3000,
            stages: [
                { target: 500,  duration: '20s' },
                { target: 1000, duration: '20s' },
                { target: 2000, duration: '20s' },
                { target: 3000, duration: '20s' }
            ]
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