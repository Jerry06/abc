package com.example.stock.service;

import com.example.stock.domain.Ticker;
import com.example.stock.exception.InvalidTickerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by vietnguyen on 25/06/2017.
 */
@CacheConfig(cacheNames = "tickerCache")
@Service
public class TickerServiceCache {

    private static Logger LOGGER = LoggerFactory.getLogger(TickerServiceCache.class);

    private static final String database = "WIKI";
    private static final String QuandlURL = "https://www.quandl.com/api/v3/datasets/%s/%s.json";

    /**
     * get Ticker information from Quandl and caching it
     *
     * @param ticketName : is ticker symbol
     * @return
     */
    @Cacheable
    public Ticker getTicker(String ticketName) {
        try {
            LOGGER.info("Start loading " + ticketName);
            RestTemplate restTemplate = new RestTemplate();
            Ticker ticker = restTemplate.getForObject(String.format(QuandlURL, database, ticketName), Ticker.class);
            LOGGER.info("End loading " + ticketName);
            return ticker;
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("getTicker", ex);
            throw new InvalidTickerException(ticketName);
        }
    }

}
