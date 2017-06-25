package com.example.stock.rest;

import com.example.stock.domain.*;
import com.example.stock.domain.TickerPrices.TickerClosePrice;
import com.example.stock.exception.InvalidDateRangeException;
import com.example.stock.service.TickerService;
import com.example.stock.service.TickerAverageTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by vietnguyen on 23/06/2017.
 */
@RestController
@RequestMapping("/api/v2")
public class TickerController {

    private static Logger LOGGER = LoggerFactory.getLogger(TickerController.class);

    private TickerService tickerService;

    @Autowired
    public TickerController(TickerService stockService) {
        this.tickerService = stockService;
    }

    /**
     * get Close Price for a ticker symbol for a range of dates (start date and end date)
     *
     * @param tickerSymbol : required, ticker name. Ex: GE
     * @param startDate    : optional, get LocalDate.MIN when null, format: YYYY-MM-DD
     * @param endDate      : optional, get LocalDate.now() when null, format: YYYY-MM-DD
     * @return
     */
    @RequestMapping(value = "/{tickerSymbol}/closePrice", method = RequestMethod.GET)
    public TickerPrices getTickerClosePrice(@PathVariable String tickerSymbol,
                                  @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = Constants.REST_DATE_FORMAT) LocalDate startDate,
                                  @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = Constants.REST_DATE_FORMAT) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.MIN;
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (endDate.isBefore(startDate))
            throw new InvalidDateRangeException(startDate, endDate);
        Ticker ticker = tickerService.getTicker(tickerSymbol);
        List<List<String>> stockInfos = tickerService.getStockInfoLists(startDate, endDate, ticker);
        List<TickerClosePrice> closePrices = new ArrayList<>();
        closePrices.add(new TickerClosePrice(tickerSymbol, stockInfos));
        return new TickerPrices(closePrices);
    }

    @RequestMapping(value = "/{tickerSymbol}/200dma", method = RequestMethod.GET)
    public DMA200 getTickerAverage(@PathVariable String tickerSymbol,
                                   @RequestParam(value = "startDate") @DateTimeFormat(pattern = Constants.REST_DATE_FORMAT) LocalDate startDate) {
        TickerAverage tickerAverage = tickerService.getAverage(tickerSymbol, startDate, 200);
        return new DMA200(tickerAverage);
    }

    @RequestMapping(value = "/200dma", method = RequestMethod.GET)
    public DMAs200 getTicker(@RequestParam(value = "tickerSymbols") String tickerSymbols,
                             @RequestParam(value = "startDate") @DateTimeFormat(pattern = Constants.REST_DATE_FORMAT) LocalDate startDate) {
        String[] tickerSymbolArr = tickerSymbols.split(",");
        int nThreads = Runtime.getRuntime().availableProcessors();
        if (nThreads > tickerSymbolArr.length) {
            nThreads = tickerSymbolArr.length;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        Map<String, Future<TickerAverage>> futureMap = new HashMap<>();
        for (String tickerSymbol : tickerSymbolArr) {
            futureMap.put(tickerSymbol, executorService.submit(new TickerAverageTask(tickerSymbol, tickerService, startDate)));
        }
        executorService.shutdown();
        List<TickerAverage> tickerAverageList = new ArrayList<>();
        List<TickerAverageError> tickerErrorList = new ArrayList<>();
        for (Map.Entry<String, Future<TickerAverage>> entry : futureMap.entrySet()) {
            try {
                TickerAverage tickerAverage = entry.getValue().get();
                tickerAverageList.add(tickerAverage);
            } catch (Exception e) {
                LOGGER.error("future.get()", e);
                tickerErrorList.add(new TickerAverageError(entry.getKey(), e.getMessage()));
            }
        }
        DMAs200 result = new DMAs200();
        result.setDma200(tickerAverageList);
        result.setErrors(tickerErrorList);
        return result;
    }

    @RequestMapping("/")
    public String gethelloWorld() {
        return "Hello World!";
    }
}

