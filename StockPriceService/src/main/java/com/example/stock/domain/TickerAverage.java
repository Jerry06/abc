package com.example.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by vietnguyen on 24/06/2017.
 */
@Data
@AllArgsConstructor
public class TickerAverage {

    @JsonProperty("Ticker")
    private String tickerName;

    @JsonProperty("Avg")
    private Double avg;

}
