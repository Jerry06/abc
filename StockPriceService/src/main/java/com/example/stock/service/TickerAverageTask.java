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
    private TickerService tickerService;
    private LocalDate startDate;

    public TickerAverageTask(String tickerSymbol, TickerService tickerService, LocalDate startDate) {
        this.tickerSymbol = tickerSymbol;
        this.tickerService = tickerService;
        this.startDate = startDate;
    }

    @Override
    public TickerAverage call() throws InvalidTickerException, NoDataException {
        System.out.println("Start TickerAverageTask with ticket " + tickerSymbol + " : " + LocalDateTime.now());
        TickerAverage tickerAverage = tickerService.getAverage(tickerSymbol, startDate, 200);
        System.out.println("End TickerAverageTask with ticket " + tickerSymbol + " : " + LocalDateTime.now());
        return tickerAverage;
    }
}
