# Stock Price Service
* Author:  vietnguyen
* Email: nxviet05@gmai.com
* Sentifi Programming Test

##Introduction
* This is a Microservice that provides a REST API which provides information about Ticker, ClosePrice.
* The datasource is getting on https://www.quandl.com/api/v3/datasets/WIKI/

## Technical Stacks
* Java8, SpringBoot, Maven, Lombok(need plugin if open source by IDE)
* JCache with EhCache provider
* Testing with SpringBootTest
* API Document with Swagger

## Running the app
- Double click stockPriceService.jar in Deploy Folder or run it in command line
- The APis information can check at http://localhost:8080/swagger-ui.html
- Need to kill Java process if want to close app.

## Example API URL and response

- GET localhost:8080/api/v2/GE/closePrice?startDate=2017-06-22&endDate=2017-06-23

```json
{
  "Prices": [
    {
      "Ticker": "GE",
      "DateClose": [
        [
          "2017-06-23",
          "27.57"
        ],
        [
          "2017-06-22",
          "27.55"
        ]
      ]
    }
  ]
}
```
- GET localhost:8080/api/v2/FB/200dma?startDate=2016-09-03

```json
{
  "200dma": {
    "Ticker": "FB",
    "Avg": 134.07920000000001
  }
}
```
- GET localhost:8080/api/v2/200dma?startDate=2016-09-08&tickerSymbols=FB,GE,AC
```json
{
  "numberSuccess": 2,
  "numberFail": 1,
  "errors": [
    {
      "Ticker": "AC",
      "Error": "com.example.stock.exception.InvalidTickerException: Ticker symbol 'AC' is invalid. Or request to www.quandl.com is limited."
    }
  ],
  "200dma": [
    {
      "Ticker": "FB",
      "Avg": 134.07920000000001
    },
    {
      "Ticker": "GE",
      "Avg": 29.7798
    }
  ]
}
```





