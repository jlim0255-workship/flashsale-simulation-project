HOW TO RUN K6 Script

[WORKING]: docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-naive.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/naive.js

Naive result:
PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'
count
-------
109
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'    
event_id | available | version
----------+-----------+---------
1 |        -9 |       0
(1 row)


Milestone 3: [TO SHOW IDEMPOTENCY NOT OVERSOLD HERE]:
docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count(*) FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count(*) > 1 ORDER BY count(*) DESC LIMIT 10;'

Duplicate.js and pre-fix schema
docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-duplicate-pre.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/duplicate.js
PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count(*) FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count(*) > 1 ORDER BY count(*) DESC LIMIT 10;'
idempotency_key | count
-----------------+-------
fixed-57        |     6
fixed-267       |     6
fixed-311       |     5
fixed-325       |     5
fixed-262       |     5
fixed-85        |     5
fixed-304       |     5
fixed-386       |     5
fixed-133       |     4
fixed-132       |     4
(10 rows)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'
count
-------
4116
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'
event_id | available | version
----------+-----------+---------
1 |     45884 |       0
(1 row)


Duplicate.js and post-fix schema
docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-duplicate-post.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/duplicate.js

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT idempotency_key, count(*) FROM orders WHERE event_id = 1 GROUP BY idempotency_key HAVING count(*) > 1 ORDER BY count(*) DESC LIMIT 10;'                                              
idempotency_key | count
-----------------+-------
(0 rows)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'                                                                                                                                                
event_id | available | version
----------+-----------+---------
1 |     45984 |       0
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'                                                                                                                                            
count
-------
4016
(1 row)

[Highlights from the above results]:
- The naive implementation oversold the inventory, resulting in a negative available count (-9) and a total of 109 orders.
- The duplicate.js implementation with the pre-fix schema resulted in 4116 orders, with multiple idempotency keys having duplicate counts, indicating that the system allowed duplicate orders to be created.
- The duplicate.js implementation with the post-fix schema resulted in 4016 orders, with no duplicate idempotency keys, indicating that the system successfully prevented duplicate orders and maintained the correct inventory count (45984 available).

[Why the post schema fix works?]:
The same key must return the same order.

naive flow: 
SELECT to see if the key exists, if not INSERT **(check-then-act)** -> this is not atomic, so multiple threads can see that the key does not exist and insert duplicate orders.

fixed flow:
always INSERT and let the constraint on idempotency_key column reject. The unique index is created on the idempotency_key column, so if a duplicate key is inserted, the database will reject it and return an error. **(check-and-act)**
This ensures that only one order is created for each unique idempotency key, preventing duplicates and maintaining the correct inventory count.

M4: with locks
Below are the results for each run with different locking strategy, no oversold issue spotted from the results.

[ATOMIC CONDITIONAL UPDATE]

docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-atomic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'
event_id | available | version
----------+-----------+---------
1 |         0 |       0
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'        
count
-------
100
(1 row)

[PESSIMISTIC LOCK]
docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-pessimistic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'    
event_id | available | version
----------+-----------+---------
1 |         0 |       0
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'
count
-------
100
(1 row)

[OPTIMISTIC LOCK]
docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-optimistic.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/withLocks.js

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'
event_id | available | version
----------+-----------+---------
1 |         0 |     100
(1 row)

PS C:\Users\Jack\Monash\MyStuff\flashsale-simulation-backend-project\src\main\java\com\jlim\flashsale_simulation> docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'
count
-------
100
(1 row)

Pessimistic is 2.6x slower, and the reason is the interesting part.

99.7% of these requests are rejections. Atomic rejects by running UPDATE ... WHERE available > 0, matching zero rows, taking no row lock. Optimistic rejects even more cheaply — a plain SELECT, sees 0, returns immediately, never touches a lock.

Pessimistic takes the exclusive lock on every request, including the ones it's about to reject. SELECT ... FOR UPDATE doesn't care that available is 0; it queues you for the lock first and lets you find out afterward. So all 16,348 requests serialize on one row to be told "no."

That's a genuine production insight: pessimistic locking taxes the rejection path, and in a flash sale rejections are nearly all your traffic.

[HOW TO RUN]:
1. set your strategy in application.properties, purchase.strategy = (naive/atomic/pssimistic/optimistic)
2. docker compose -v down
3. docker compose up
4. start application
5. POST admin reset endpoint http://localhost:8080/admin/reset, {"eventId":1,"capacity":100}
6. execute this command: docker run --rm -p 5665:5665 -e K6_WEB_DASHBOARD=true -e K6_WEB_DASHBOARD_EXPORT=/reports/report-{your-strategy}.html -v "${PWD}/k6:/scripts" -v "${PWD}/k6:/reports" grafana/k6 run /scripts/{your-strategy}.js
7. after the threshold period: to see the results in db:
   a. Using terminal to access db:
   *. check the remaining available tickets: docker compose exec db psql -U flashsale -d flashsale -c 'SELECT * FROM inventory WHERE event_id = 1;'
   *. check sold tickets orders: docker compose exec db psql -U flashsale -d flashsale -c 'SELECT count(*) FROM orders WHERE event_id = 1;'

   b. Or connect to universal db manager (e.g. DBeaver) to see rows
8. Check the produced html for precise performance detail.


[When we access this endpoint: http://localhost:8080/actuator/metrics/hikaricp.connections.max]
We can see the max connection pool size, which is 10. This is the default value for HikariCP connection pool in Spring Boot. If we want to change this value, we can set the property spring.datasource.hikari.maximum-pool-size in application.properties file.

{"availableTags":[{"tag":"pool","values":["HikariPool-1"]}],"description":"Max connections","measurements":[{"statistic":"VALUE","value":10.0}],"name":"hikaricp.connections.max"}