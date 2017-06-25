package com.example.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 200 day moving average for multi ticker symbol
 */
@Data
public class DMAs200 {

    private int numberSuccess;

    public int getNumberFail() {
        return errors.size();
    }

    public int getNumberSuccess() {
        return dma200.size();
    }

    private int numberFail;

    @JsonProperty("200dma")
    private List<TickerAverage> dma200 = new ArrayList<>();

    private List<TickerAverageError> errors = new ArrayList<>();

}
