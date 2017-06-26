package com.example.stock.service;

import com.example.stock.domain.Ticker;
import com.example.stock.domain.TickerAverage;
import com.example.stock.exception.InvalidTickerException;
import com.example.stock.exception.NoDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

import static java.util.stream.Collectors.toList;

/**
 * Created by vietnguyen on 23/06/2017.
 * The class handles business code of Ticker
 */
@Service
public class TickerService {

    private TickerServiceCache tickerServiceCache;

    @Autowired
    public TickerService(TickerServiceCache tickerServiceCache) {
        this.tickerServiceCache = tickerServiceCache;
    }

    /**
     * get Ticker information from Quandl and caching it
     *
     * @param ticketName : is ticker symbol
     * @return
     */
    public Ticker getTicker(String ticketName) {
        return tickerServiceCache.getTicker(ticketName);
    }

    /**
     * validation ticker is having moving average from the start date
     *
     * @param ticker
     * @param startDate : start date
     * @param dmaDays   : number of days moving average
     */
    public void validateData(Ticker ticker, LocalDate startDate, int dmaDays) {
        long count = ticker.getStockList().parallelStream()
                .filter(p -> !startDate.isAfter(p.getDate()))
                .count();
        if (count < dmaDays) {
            String suggestDate = "N/A";
            if (ticker.getStockList().size() > dmaDays) {
                suggestDate = ticker.getStockList().get(dmaDays - 1).getDate().toString();
            }
            throw new NoDataException(startDate, suggestDate);
        }
    }

    /**
     * get stock info list of ticker in range between start & end date
     *
     * @param startDate
     * @param endDate
     * @param ticker
     * @return
     */
    public List<List<String>> getCloseInfoList(LocalDate startDate, LocalDate endDate, Ticker ticker) {
        return ticker.getStockList().stream()
                .filter(p -> !startDate.isAfter(p.getDate())
                        && !endDate.isBefore(p.getDate()))
                .map(p -> p.toCloseInfo())
                .collect(toList());
    }

    /**
     * get average price of a ticker from start date within number of days moving average
     *
     * @param tickerSymbol
     * @param startDate
     * @param dmaDays      : number of days moving average
     * @return
     * @throws InvalidTickerException
     * @throws NoDataException
     */
    public TickerAverage getAverage(String tickerSymbol, LocalDate startDate, int dmaDays) throws InvalidTickerException, NoDataException {
        Ticker ticker = tickerServiceCache.getTicker(tickerSymbol);
        this.validateData(ticker, startDate, dmaDays);
        OptionalDouble average = ticker.getStockList().parallelStream()
                .filter(p -> !startDate.isAfter(p.getDate()))
                .limit(dmaDays)
                .mapToDouble(p -> p.getClosePrice()).average();
        if (average.isPresent()) {
            return new TickerAverage(tickerSymbol, average.getAsDouble());
        }
        throw new NoDataException(startDate, "N/A");
    }
}
