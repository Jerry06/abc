package com.example.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 200 day moving average
 * Created by vietnguyen on 24/06/2017.
 */
@Data
@AllArgsConstructor
public class DMA200 {

    @JsonProperty("200dma")
    private TickerAverage dma200;
}
