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