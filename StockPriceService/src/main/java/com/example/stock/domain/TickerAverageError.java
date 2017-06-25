package com.example.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by vietnguyen on 25/06/2017.
 */
@AllArgsConstructor
public class TickerAverageError {

    @JsonProperty("Ticker")
    private String tickerName;

    @JsonProperty("Error")
    private String errorMsg;
}
