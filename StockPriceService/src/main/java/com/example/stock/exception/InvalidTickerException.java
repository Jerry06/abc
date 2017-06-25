package com.example.stock.exception;

/**
 * Created by vietnguyen on 24/06/2017.
 */
public class InvalidTickerException extends RuntimeException {

    public InvalidTickerException(String tickerSymbol) {
        super(String.format("Ticker symbol '%s' is invalid. Or request to www.quandl.com is limited.", tickerSymbol));
    }
}
