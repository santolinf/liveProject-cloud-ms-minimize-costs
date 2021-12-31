# Review the Problem - Multiple Same Requests

After starting all microservices and running the Clusters microservice search from Postman, I observed the
following.

This is the result of the search:
[JSON Response](flights_itineray_search_result.json)

The api-catalog microservice logs show the same requests for the same city parameter:
[api-catalog logs](api-catalog-service-logs.txt)

| City Code | Number of log entries |
|-----------|----|
| BUE       | 82 |
| MIA       | 82 |
