# Review the Problem

## <a id="reduce-milestone1"></a> Reduce Traffic - Milestone 1

After starting all microservices and running the Clusters microservice search from Postman, I observed the
following.

This is the result of the search:
[JSON Response](flights-itineray-search-result.json)

The api-catalog microservice logs show the same requests for the same city parameter:
[api-catalog logs](api-catalog-service-logs.txt)

| City Code | Number of log entries |
|-----------|----|
| BUE       | 82 |
| MIA       | 82 |

## <a id="compress-milestone1"></a> Compress Communications - Milestone 1

| Microservice           | JSON Search result in bytes                           |
|------------------------|-------------------------------------------------------| 
| api-pricing            | [8,153 bytes](api-pricing-search-result.json)       |
| api-catalog            | [245 bytes](api-catalog-search-result.json)           |
| api-provider-alpha     | [16,246 bytes](api-provider-alpha-search-result.json) |
| api-provider-beta      | [16,242 bytes](api-provider-beta-search-result.json)  |
| api-itineraries-search | [32,472 bytes](api-itineraries-search-result.json)    |
| api-clusters           | [19,962 bytes](api-clusters-search-result.json)       |

