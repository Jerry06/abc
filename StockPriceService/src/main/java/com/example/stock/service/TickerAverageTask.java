package com.example.stock.service;

import com.example.stock.domain.Ticker;
import com.example.stock.domain.TickerAverage;
import com.example.stock.exception.InvalidTickerException;
import com.example.stock.exception.NoDataException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.OptionalDouble;
import java.util.concurrent.Callable;

/**
 * Created by vietnguyen on 24/06/2017.
 */
public class TickerAverageTask implements Callable<TickerAverage> {

    private String tickerSymbol;
    private TickerService stockService;
    private LocalDate startDate;

    public TickerAverageTask(String tickerSymbol, TickerService stockService, LocalDate startDate) {
        this.tickerSymbol = tickerSymbol;
        this.stockService = stockService;
        this.startDate = startDate;
    }

    @Override
    public TickerAverage call() throws InvalidTickerException, NoDataException {
        System.out.println("Start TickerAverageTask with ticket " + tickerSymbol + " : " + LocalDateTime.now());
        Ticker ticker = stockService.getTicker(tickerSymbol);
        OptionalDouble average = ticker.getStockInfos().parallelStream()
                .filter(p -> !startDate.isAfter(p.getDate()))
                .limit(200)
                .mapToDouble(p -> p.getClosePrice()).average();
        if (average.isPresent()) {
            return new TickerAverage(tickerSymbol, average.getAsDouble());
        }
        System.out.println("End TickerAverageTask with ticket " + tickerSymbol + " : " + LocalDateTime.now());
        return null;
    }
}
