package com.example.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vietnguyen on 24/06/2017.
 */
@Data
@AllArgsConstructor
public class TickerPrices {

    @JsonProperty("Prices")
    private List<TickerClosePrice> closePriceList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class TickerClosePrice {

        @JsonProperty("Ticker")
        private String tickerName;

        @JsonProperty("DateClose")
        private List<List<String>> closeInfoList;
    }

}