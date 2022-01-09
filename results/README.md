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

## <a id="compress-milestone1"></a> Compress Communications Part

### Milestone 1 - un-compressed search result sizes

| Microservice           | JSON Search result size                         |
|------------------------|-------------------------------------------------| 
| api-pricing            | [4.78kb](api-pricing-search-result.json)        |
| api-catalog            | [0.28kb](api-catalog-search-result.json)        |
| api-provider-alpha     | [8.87kb](api-provider-alpha-search-result.json) |
| api-provider-beta      | [8.86kb](api-provider-beta-search-result.json)  |
| api-itineraries-search | [17.59kb](api-itineraries-search-result.json)   |
| api-clusters           | [9.89kb](api-clusters-search-result.json)       |

### Milestone 2 - compressed search result sizes

| Microservice           | Compressed JSON Search result size                           |
|------------------------|--------------------------------------------------------------| 
| api-pricing            | **1.29kb** (before 4.78kb)                                   |
| api-catalog            | **0.29kb** (slight increase from before due to more headers) |
| api-provider-alpha     | **1.41kb** (before 8.87kb)                                   |
| api-provider-beta      | **1.44kb** (before 8.86kb)                                   |
| api-itineraries-search | **2.35kb** (before 17.59kb)                                  |
| api-clusters           | **1.84kb** (before 9.89kb)                                   |

To calculate the *compressed* response size use `cUrl` instead of Postman.

Use the following commands by changing the data file name and endpoint URL, if required.

Shown below is an example POST request for the Pricing microservice.

    curl -i -XPOST -H 'Accept-Encoding: gzip' -H 'Content-Type: application/json' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' -d '@request.data/pricing.json' 'http://localhost:5070/api/flights/pricing/itineraries'

Shown below is an example GET request for the Catalog microservice.

    curl -i -H 'Accept-Encoding: gzip' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' 'http://localhost:6070/api/flights/catalog/city/BUE'

Shown below is an example GET request for the Alpha Provider microservice.

    curl -i -H 'Accept-Encoding: gzip' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' 'http://localhost:8070/api/flights/provider/alpha/itineraries?from=BUE,MIA&to=MIA,BUE&departure=2022-01-21,2022-01-30&adults=1&children=1&infants=1&amount=10'

Shown below is an example GET request for the Beta Provider microservice.

    curl -i -H 'Accept-Encoding: gzip' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' 'http://localhost:9070/api/flights/provider/beta/itineraries?from=BUE,MIA&to=MIA,BUE&departure=2022-01-21,2022-01-30&adults=1&children=1&infants=1&amount=10'

Shown below is an example GET request for the Itinerary Search microservice.

    curl -i -H 'Accept-Encoding: gzip' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' 'http://localhost:7070/api/flights/itineraries-search/itineraries?from=BUE,MIA&to=MIA,BUE&departure=2022-01-21,2022-01-30&adults=1&children=1&infants=1&amount=10'

Shown below is an example GET request for the Clusters microservice.

    curl -i -H 'Accept-Encoding: gzip' -w '\n\nResponse Size (bytes): %{size_download} + %{size_header}\n' 'http://localhost:4070/api/flights/clusters/itineraries?from=BUE,MIA&to=MIA,BUE&departure=2022-01-21,2022-01-30&adults=1&children=1&infants=1&amount=10'

## <a id="prevent-duplicate-milestone1"></a> Prevent Duplicates Part

### Milestone 1 - Clusters search result

[API Clusters Search result one](./duplicate.result/api-clusters-search-one.json)

[API Clusters Search result two](./duplicate.result/api-clusters-search-two.json)

