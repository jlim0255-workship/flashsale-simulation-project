[//]: # (HOW TO RUN K6 Script)

[//]: # ()
[//]: # ([WORKING]: docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-naive.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/naive.js)

[//]: # ()
[//]: # (Naive result:)

[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;')

[//]: # (count)

[//]: # (-------)

[//]: # (109)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'    )

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |        -9 |       0)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # ()
[//]: # (Milestone 3: [TO SHOW IDEMPOTENCY NOT OVERSOLD HERE]:)

[//]: # (docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count&#40;*&#41; FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count&#40;*&#41; > 1 ORDER BY count&#40;*&#41; DESC LIMIT 10;')

[//]: # ()
[//]: # (Duplicate.js and pre-fix schema)

[//]: # (docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-duplicate-pre.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/duplicate.js)

[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count&#40;*&#41; FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count&#40;*&#41; > 1 ORDER BY count&#40;*&#41; DESC LIMIT 10;')

[//]: # (idempotency_key | count)

[//]: # (-----------------+-------)

[//]: # (fixed-57        |     6)

[//]: # (fixed-267       |     6)

[//]: # (fixed-311       |     5)

[//]: # (fixed-325       |     5)

[//]: # (fixed-262       |     5)

[//]: # (fixed-85        |     5)

[//]: # (fixed-304       |     5)

[//]: # (fixed-386       |     5)

[//]: # (fixed-133       |     4)

[//]: # (fixed-132       |     4)

[//]: # (&#40;10 rows&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;')

[//]: # (count)

[//]: # (-------)

[//]: # (4116)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;')

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |     45884 |       0)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # ()
[//]: # (Duplicate.js and post-fix schema)

[//]: # (docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-duplicate-post.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/duplicate.js)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count&#40;*&#41; FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count&#40;*&#41; > 1 ORDER BY count&#40;*&#41; DESC LIMIT 10;'                                              )

[//]: # (idempotency_key | count)

[//]: # (-----------------+-------)

[//]: # (&#40;0 rows&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'                                                                                                                                                )

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |     45984 |       0)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;'                                                                                                                                            )

[//]: # (count)

[//]: # (-------)

[//]: # (4016)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # ([Highlights from the above results]:)

[//]: # (- The naive implementation oversold the inventory, resulting in a negative available count &#40;-9&#41; and a total of 109 orders.)

[//]: # (- The duplicate.js implementation with the pre-fix schema resulted in 4116 orders, with multiple idempotency keys having duplicate counts, indicating that the system allowed duplicate orders to be created.)

[//]: # (- The duplicate.js implementation with the post-fix schema resulted in 4016 orders, with no duplicate idempotency keys, indicating that the system successfully prevented duplicate orders and maintained the correct inventory count &#40;45984 available&#41;.)

[//]: # ()
[//]: # ([Why the post schema fix works?]:)

[//]: # (The same key must return the same order.)

[//]: # ()
[//]: # (naive flow: )

[//]: # (SELECT to see if the key exists, if not INSERT **&#40;check-then-act&#41;** -> this is not atomic, so multiple threads can see that the key does not exist and insert duplicate orders.)

[//]: # ()
[//]: # (fixed flow:)

[//]: # (always INSERT and let the constraint on idempotency_key column reject. The unique index is created on the idempotency_key column, so if a duplicate key is inserted, the database will reject it and return an error. **&#40;check-and-act&#41;**)

[//]: # (This ensures that only one order is created for each unique idempotency key, preventing duplicates and maintaining the correct inventory count.)

[//]: # ()
[//]: # (M4: with locks)

[//]: # (Below are the results for each run with different locking strategy, no oversold issue spotted from the results.)

[//]: # ()
[//]: # ([ATOMIC CONDITIONAL UPDATE])

[//]: # ()
[//]: # (docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-atomic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;')

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |         0 |       0)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;'        )

[//]: # (count)

[//]: # (-------)

[//]: # (100)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # ([PESSIMISTIC LOCK])

[//]: # (docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-pessimistic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'    )

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |         0 |       0)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;')

[//]: # (count)

[//]: # (-------)

[//]: # (100)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # ([OPTIMISTIC LOCK])

[//]: # (docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-optimistic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;')

[//]: # (event_id | available | version)

[//]: # (----------+-----------+---------)

[//]: # (1 |         0 |     100)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;')

[//]: # (count)

[//]: # (-------)

[//]: # (100)

[//]: # (&#40;1 row&#41;)

[//]: # ()
[//]: # (Pessimistic is 2.6x slower, and the reason is the interesting part.)

[//]: # ()
[//]: # (99.7% of these requests are rejections. Atomic rejects by running UPDATE ... WHERE available > 0, matching zero rows, taking no row lock. Optimistic rejects even more cheaply — a plain SELECT, sees 0, returns immediately, never touches a lock.)

[//]: # ()
[//]: # (Pessimistic takes the exclusive lock on every request, including the ones it's about to reject. SELECT ... FOR UPDATE doesn't care that available is 0; it queues you for the lock first and lets you find out afterward. So all 16,348 requests serialize on one row to be told "no.")

[//]: # ()
[//]: # (That's a genuine production insight: pessimistic locking taxes the rejection path, and in a flash sale rejections are nearly all your traffic.)

[//]: # ()
[//]: # ([HOW TO RUN]:)

[//]: # (1. set your strategy in application.properties, purchase.strategy = &#40;naive/atomic/pssimistic/optimistic&#41;)

[//]: # (2. docker compose -v down)

[//]: # (3. docker compose up)

[//]: # (4. start application)

[//]: # (5. POST admin reset endpoint http://localhost:8080/admin/reset, {"eventId":1,"capacity":100})

[//]: # (6. execute this command: docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-{your-strategy}.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/{your-strategy}.js)

[//]: # (7. after the threshold period: to see the results in db:)

[//]: # (   a. Using terminal to access db:)

[//]: # (   *. check the remaining available tickets: docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;')

[//]: # (   *. check sold tickets orders: docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count&#40;*&#41; FROM orders WHERE event_id = 1;')

[//]: # ()
[//]: # (   b. Or connect to universal db manager &#40;e.g. DBeaver&#41; to see rows)

[//]: # (8. Check the produced html for precise performance detail.)

[//]: # ()
[//]: # ()
[//]: # ([When we access this endpoint: http://localhost:8080/actuator/metrics/hikaricp.connections.max])

[//]: # (We can see the max connection pool size, which is 10. This is the default value for HikariCP connection pool in Spring Boot. If we want to change this value, we can set the property spring.datasource.hikari.maximum-pool-size in application.properties file.)

[//]: # ()
[//]: # ({"availableTags":[{"tag":"pool","values":["HikariPool-1"]}],"description":"Max connections","measurements":[{"statistic":"VALUE","value":10.0}],"name":"hikaricp.connections.max"})

# Flash-Sale Concurrency Simulation

A single-endpoint ticket purchase service, built to demonstrate and then fix
concurrency bugs under load.

The service itself is deliberately small. The deliverable is the results
table and the explanation of why each number came out the way it did. The
approach is: build the broken version first, load-test it until it visibly
fails, then fix it four different ways and measure each fix against the
same baseline.

## Stack

Spring Boot 3 (Java 21) · Postgres 16 · JdbcTemplate · k6 · Docker Compose

JdbcTemplate rather than JPA on purpose: this project is about exactly what
SQL reaches the database and what locks it takes. Hibernate's dirty
checking, flush timing, and first-level cache would sit between the code and
that question.

## Architecture

```
PurchaseController
   └─ PurchaseService              no @Transactional — catches DuplicateKeyException
        └─ PurchaseTransaction     @Transactional — claim, decrement, confirm
             └─ InventoryStrategy  naive | atomic | pessimistic | optimistic
```

`PurchaseService` and `PurchaseTransaction` are separate beans because
`@Transactional` is applied by a proxy. A call to `this.someMethod()`
bypasses the proxy entirely and the annotation is silently ignored. More
importantly, a duplicate-key violation aborts the whole Postgres
transaction — every subsequent statement fails with "current transaction is
aborted" — so the exception cannot be caught and handled inside the
transaction that raised it. The catch has to happen outside, in a caller
that is not itself transactional.

Strategies are selected by `purchase.strategy` in `application.properties`,
wired with `@ConditionalOnProperty`. Exactly one strategy bean exists at
runtime, so a typo in the property fails at startup rather than at request
time.

### Schema

```sql
events     (id, name, capacity, created_at)
inventory  (event_id PK/FK, available, version)
orders     (id, event_id, user_id, idempotency_key NOT NULL UNIQUE,
            status, created_at)
```

`inventory` is separate from `events` so the contended row stays narrow.
`version` is used only by the optimistic strategy.

---

# Milestone 1: Skeleton

Naive strategy, no idempotency, verified sequentially only.

| Check | Result |
|---|---|
| Capacity 3, four sequential purchases | 3 × 200, then 409 |
| Rows in `orders` | 3 |
| Reset restores `available` | Yes |

The naive strategy passes every one of these. That is not evidence of
anything: sequential requests never overlap, so only one thread is ever
inside the purchase path and the race cannot occur. This milestone exists to
confirm the plumbing works, not the logic.

---

# Milestone 2: — The race condition

Strategy `naive` · capacity 100 · 500 VUs · 30s · no sleep between iterations

## Predictions (recorded before the run)

| Metric | Predicted | Actual |
|---|---|---|
| Orders | 100–200 | 109 |
| `available` | never exactly 0 | −9 |
| Throughput | "hundreds/sec is heavy" | 1,499/s |

## Results

| Metric | Value |
|---|---|
| Requests | 45,514 |
| Throughput | 1,499 req/s |
| HTTP 200 | 109 |
| HTTP 409 | 45,405 |
| **Rows in `orders`** | **109** |
| **`inventory.available`** | **−9** |
| **Oversell** | **+9 (9%)** |
| avg / p90 / p95 / p99 | 328 / 491 / 582 / 1,069 ms |
| max | 2,135 ms |

`100 − 109 = −9`. The counter and the order count agree exactly.

## Why 109

109 threads passed the `available > 0` check and each was individually
correct: every one of them read a genuinely positive, genuinely committed
value. Nine of them read a value that no other transaction had yet committed
a decrement against, even though several were already in flight to claim it.
Postgres read-committed shows the last committed snapshot, and in-flight
work is invisible.

It is 109 and not 500 because the damage is bounded by the size of the race
window, not by the number of requests. The window between the `SELECT` and
the `UPDATE` is roughly a millisecond. Only requests whose read landed inside
that window as inventory crossed zero could double-book; roughly nine
threads fit. The other ~45,400 requests arrived after `available ≤ 0` was
committed, read a non-positive value, and were correctly rejected with 409.

The arithmetic was never wrong. `UPDATE ... SET available = available - 1`
takes a row-level exclusive lock, so concurrent updates serialize and each
one re-reads the current committed value before applying the subtraction. No
decrement was ever lost. `SELECT` takes no lock at all, so the *check* is
what failed, not the write.

## Lost update vs. check-then-act

Two distinct races, frequently conflated.

**Lost update** happens with `SET available = <value computed in Java>`. Two
threads read 10, both write 9. Two tickets sold, one decrement recorded —
the second write destroys the first thread's work.

**Check-then-act** happens with `SET available = available - 1`. The write
itself is safe, for the reason above. The race is entirely in the check: N
threads can all read `available = 1`, all pass `if (available > 0)`, and all
proceed to decrement.

This code has the second. The `−9` result proves it: if writes had been
lost, `available` would have ended *higher* than `100 − orders`. It ended
exactly at `100 − 109`, so every decrement landed.

The fix, applied in M4, is to move the condition out of Java and into the
SQL so that check and write happen inside the same locked statement.

---

# Milestone 3: Idempotency

## The bug

A different failure from oversell. Oversell is many users racing over one
counter. This is **one user's request arriving twice** — a network timeout,
an impatient double-click, a client-side retry. Without protection, each
attempt creates its own order.

## The fix

Inside one transaction, in this order:

```
1. INSERT order (status PENDING, with idempotency_key)   ← the claim
2. strategy.tryDecrement()
      false → throw SoldOutException  (rollback removes the claim)
3. UPDATE status = CONFIRMED
```

**Insert first**, because the claim has to be staked before any work is
done. And because the insert shares a transaction with the decrement, a
sold-out result rolls the claim back and releases the key — so a retry after
sellout is a fresh attempt rather than a permanently burned key.

The obvious implementation is broken in exactly the way M2 was broken.
`SELECT` to see whether the key exists, and if not `INSERT`, is check-then-act:
two concurrent retries both find nothing, both insert, two orders. There is no
point at which application code can hold the check and the write together.

So the code does not check. It attempts the insert and lets the unique index
reject it. The condition is enforced *inside* the write, atomically, by the
database. This is the same move as `AND available > 0` in the atomic
strategy — push the condition into the write and let the database arbitrate.

The lookup after a `DuplicateKeyException` is safe because Postgres blocks
the second inserter on the unique index until the first transaction
resolves. If the first commits, the second gets the exception and the first
row is already visible. If the first rolls back, the second insert simply
succeeds. There is no window in which a duplicate is reported but the
original cannot be found.

## Results: capacity 50,000, 500 VUs, 30s

Capacity set high so nothing sells out and every request performs a full write.

| | pre-fix | post-fix |
|---|---|---|
| Requests | 4,116 | 4,524 |
| HTTP 200 | 4,116 (100%) | 4,524 (100%) |
| **Orders created** | **4,116** | **4,016** |
| **Deduplicated** | **0** | **508** |
| Duplicate keys in `orders` | yes, up to 6 per key | **0** |
| `available` | 45,884 | 45,984 |
| Throughput | 121/s | 135/s |
| avg latency | 3,855 ms | 3,476 ms |

Pre-fix, requests and orders are 1:1 — every reused key produced another
order. Post-fix, 508 requests hit an existing key, received the original
order back, and created nothing.

```
idempotency_key | count
----------------+-------
fixed-57        |     6
fixed-267       |     6
fixed-311       |     5
...
```

`fixed-57` produced six separate orders for what was one logical purchase.

Note that HTTP 200 no longer means "a ticket was sold". It means "your
request was honoured", which may mean "and it was already honoured earlier".

## Why the bug did not reproduce at capacity 100

A duplicate row only exists if its transaction commits, and the transaction
only commits while tickets remain. That window is the first second or so of
a 30-second run. After sellout every reused key rolls back, leaving no row
behind. The vulnerable window and the contended window are the same window,
which is why the bug needs a sale that never ends in order to show itself.

## The cost

Insert-first idempotency makes the *rejection* path more expensive. Before,
a sold-out request did one `SELECT` and returned. Now every request inserts a
claim row first, discovers it is sold out, and rolls back. In the
capacity-100 runs this showed up as roughly a 20% throughput drop.

It is the right trade — thousands of phantom orders would be far worse — but
it is not free.

---

# Milestone 4: Four strategies

## How each one works

### naive

```sql
SELECT available FROM inventory WHERE event_id = ?;      -- no lock
-- if (available > 0) in Java
UPDATE inventory SET available = available - 1 WHERE event_id = ?;
```

The check reads an unlocked snapshot; by the time the write runs, that
snapshot may be stale. Oversells.

### atomic

```sql
UPDATE inventory SET available = available - 1
WHERE event_id = ? AND available > 0;
-- rows affected: 1 → sold, 0 → sold out
```

The condition moves out of Java and into the `WHERE` clause, so the check
and the write happen inside a single locked statement. One round trip, no
window.

### pessimistic

```sql
SELECT available FROM inventory WHERE event_id = ? FOR UPDATE;  -- locks
-- if (available > 0) in Java
UPDATE inventory SET available = available - 1 WHERE event_id = ?;
```

`FOR UPDATE` takes the exclusive row lock at read time and holds it until
commit, so the read is no longer a snapshot — no other transaction can
modify the row in between. This depends entirely on
`PurchaseTransaction.doPurchase` being `@Transactional`; without a
surrounding transaction the lock would release immediately and the strategy
would degrade to naive.

### optimistic

```sql
SELECT available, version FROM inventory WHERE event_id = ?;   -- no lock
UPDATE inventory SET available = available - 1, version = version + 1
WHERE event_id = ? AND version = ?;
-- rows == 1 → won the race
-- rows == 0 → another transaction committed first, retry (max 5)
```

No lock is taken. The version column detects interference after the fact: if
it changed between the read and the write, the `WHERE` matches nothing and
the attempt is retried against fresh data.

**Open decision:** exhausting the retry budget currently returns `false`,
which surfaces as `SOLD_OUT`. That is a lie — inventory may be plentiful and
the thread simply lost five races in a row. A distinct exception mapped to
503 would be more honest. This did not occur in any run below (see
"Effective concurrency" for why), so it is documented rather than fixed.

## Correctness — capacity 100, 500 VUs, 30s

| Strategy | Orders | `available` | Oversell |
|---|---|---|---|
| naive | 109 | −9 | **+9** |
| atomic | 100 | 0 | 0 |
| pessimistic | 100 | 0 | 0 |
| optimistic | 100 | 0 | 0 |

All three fixes are correct. Optimistic additionally finished with
`version = 100`: the version increments only on a winning decrement, so it
independently counts the tickets sold.

## Throughput — capacity 100 (rejection-dominated)

99.7% of these requests are rejections, so this table measures the
**rejection** path, not the contended write path.

| Strategy | req/s | avg | p95 | p99 |
|---|---|---|---|---|
| naive | 1,499 | 328 ms | 582 | 1,069 |
| optimistic | 1,458 | 336 ms | 651 | 1,283 |
| atomic | 1,385 | 352 ms | 605 | 1,134 |
| **pessimistic** | **530** | **919 ms** | 1,936 | 2,317 |

Pessimistic is 2.6× slower than the others *in this workload*, and the
reason is specific to rejections.

Atomic rejects by running an `UPDATE` whose `WHERE` matches zero rows — no
row lock is taken. Optimistic rejects even more cheaply: an unlocked
`SELECT`, sees zero, returns immediately, never touches a lock. Pessimistic
takes the exclusive lock on **every** request, including the ones it is
about to reject. `SELECT ... FOR UPDATE` does not care that `available` is
already 0; it queues for the lock first and finds out afterwards. So all
16,348 requests serialize on one row in order to be told "no".

In a flash sale, rejections are nearly all the traffic. Pessimistic locking
taxes precisely the path that dominates.

**Anomaly:** pessimistic's check pass rate was 49.83% rather than the
expected 50%, implying roughly 56 requests returned neither 200 nor 409, and
its reported minimum latency was 0 ms. The likely cause is HikariCP
connection pool exhaustion under ~900 ms lock holds. Unconfirmed.

## Throughput: capacity 50,000 (write-dominated)

Nothing sells out, so every request performs a full contended write.

| Strategy | req/s | avg | p95 | implied lock hold |
|---|---|---|---|---|
| naive | 135 | 3,476 ms | 4,437 | ~7.4 ms |
| **atomic** | **118** | 3,985 ms | 5,069 | ~8.5 ms |
| optimistic | 104 | 4,378 ms | 5,860 | ~9.6 ms |
| pessimistic | 99 | 4,702 ms | 6,282 | ~10.1 ms |

Zero non-200 responses. No 409s, no errors, no retry exhaustion.

## The main finding

At capacity 100 the strategies differ by 2.6×. At capacity 50,000 they
differ by 19% — and naive, with no protection whatsoever, sits in the same
band as all three fixes.

Every one of these strategies holds an exclusive lock on the same
`inventory` row from the moment of decrement until commit, and commits on
the same row serialize. Throughput is therefore just the reciprocal of how
long the lock is held, and the measured ordering matches lock-hold duration
exactly: atomic takes the lock at the `UPDATE` and holds it shortest;
optimistic does the same but wastes read round trips first; pessimistic
grabs it a full round trip earlier at `SELECT ... FOR UPDATE` and holds it
longest.

**A hot-row write bottleneck cannot be fixed by choosing a better locking
strategy.** Roughly 120 req/s is the single-row write ceiling on this setup,
and every approach in this milestone converges on it. Locking strategy
determines *correctness*, and it determines throughput on the rejection
path, but it cannot move the write ceiling — all roads lead to the same
serialization point.

This is the entire motivation for Milestone 5: to go faster, the counter has
to leave the contended row.

## Little's Law

```
throughput = concurrency ÷ latency
```

| Run | 500 ÷ latency | measured |
|---|---|---|
| naive (cap 100) | 1,524 | 1,499 |
| atomic (cap 100) | 1,420 | 1,385 |
| pessimistic (cap 100) | 544 | 530 |
| optimistic (cap 100) | 1,488 | 1,458 |

These three numbers are not independent — fix any two and the third
follows. That makes the law a useful check: a measured throughput far from
what latency and concurrency predict means something other than the assumed
mechanism is at work.

## Effective concurrency is 10, not 500

```
GET /actuator/metrics/hikaricp.connections.max  →  10.0
```

HikariCP defaults to a pool of 10. At most 10 threads are ever inside the
database; the remaining 490 virtual users are queued waiting for a
connection, which accounts for most of the ~4 second latency in the
capacity-50,000 runs.

This explains why optimistic never exhausted its retry budget. It was
contending against roughly 10 rivals, not 500 — odds good enough that five
attempts always sufficed. A larger pool would raise real concurrency at the
row and would likely produce the retry storm that optimistic locking is
known for. Every number in this README is conditional on that pool size.

---

# Method notes

## Two different runs, two different questions

| | capacity 100 | capacity 50,000 |
|---|---|---|
| Question | Does it oversell? | How fast can it write? |
| Dominant path | rejection (99.7%) | contended write (100%) |
| Pass condition | orders = 100, available = 0 | — |

Numbers from the two are **not comparable** and are kept in separate tables
throughout.

## Measurement noise

Three identical runs produced 1,184 / 1,026 / 1,163 req/s — a 15% spread on
one laptop, caused by background load, Docker's storage layer, JIT warm-up,
and thermal state.

Consequently, in the capacity-50,000 table, atomic (118) versus pessimistic
(99) is probably a real difference, but optimistic (104) versus pessimistic
(99) is within noise and should not be read as an ordering. The
capacity-100 pessimistic result (530 vs ~1,400) is far outside noise and is
solid.

## Ground truth is the database

`http_req_failed` counts any 4xx as a failure, so a healthy capacity-100 run
reports ~99.7% "failed" — those are correct 409s. HTTP status counts are
never the source of truth here:

```sql
SELECT count(*) FROM orders WHERE event_id = 1;
SELECT * FROM inventory WHERE event_id = 1;
SELECT count(*) - count(DISTINCT idempotency_key) FROM orders WHERE event_id = 1;
```

If HTTP 200 count and `orders` row count disagree, that disagreement is
itself a finding — post-idempotency it is expected, because replays return
200 without creating a row.

## k6 checks

A check passes when its function returns **true**. It must assert "this is
fine", never "this is wrong".

```javascript
// Broken: passes only when something IS wrong, so a healthy run reports 0%
'unexpected': (r) => r.status !== 200 && r.status !== 409,

// Correct
'confirmed': (r) => r.status === 200,
'sold out':  (r) => r.status === 409,
```

Two mutually exclusive checks means a healthy run reports **50%**, not 100%.

---

# Running it

1. Set `purchase.strategy` in `application.properties` — `naive` | `atomic` | `pessimistic` | `optimistic`
2. `docker compose down -v && docker compose up -d`
3. Start the application
4. `POST /admin/reset` with `{"eventId":1,"capacity":100}` — or `50000` for the throughput run
5. Run k6:

```powershell
docker run --rm -p 5665:5665 `
  -e K6_WEB_DASHBOARD=true `
  -e K6_WEB_DASHBOARD_EXPORT=/reports/report-<strategy>.html `
  -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" `
  grafana/k6 run /scripts/<script>.js
```

Live dashboard at `http://localhost:5665` during the run; the HTML report
lands in `k6/`.

6. Verify against the database using the queries above or any SQL client
7. Reset before every run, or the numbers are not comparable

### Scripts

| Script | Purpose |
|---|---|
| `naive.js` | unique idempotency key per request |
| `duplicate.js` | ~20% of iterations reuse a fixed key per VU |
| `withLocks.js` | strategy comparison |

---