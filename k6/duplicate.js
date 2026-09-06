import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 500,
    duration: '30s',
};

export default function () {
    // POST to http://host.docker.internal:8080/api/v1/purchase
    // headers: X-User-Id (unique per VU — __VU gives you the VU number)
    //          Content-Type: application/json
    // body: JSON.stringify({ eventId: 1 })
    // check: status is 200 or 409, nothing else
    // TODO: ~20% of iterations reuse a fixed key per VU,
    //       80% generate a fresh one (uuidv4() or `${__VU}-${__ITER}`)
    //
    // Suggested: const key = Math.random() < 0.2
    //     ? `fixed-${__VU}`
    //     : `${__VU}-${__ITER}`;
    //
    // checks: 'confirmed' 200, 'sold out' 409
    // (drop the broken 'unexpected' check)

    const key = Math.random() < 0.2
        ? `fixed-${__VU}`
        : `${__VU}-${__ITER}`;

    const url = 'http://host.docker.internal:8080/api/v1/purchase';

    const payload = JSON.stringify({
        eventId: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-User-Id': `user-${__VU}`, // Unique ID per Virtual User
            'Idempotency-Key': key
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'confirmed': (r) => r.status === 200,
        'sold out':  (r) => r.status === 409
    });


}