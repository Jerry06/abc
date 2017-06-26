package com.example.stock.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vietnguyen on 23/06/2017.
 */
@Data
@NoArgsConstructor
@JsonDeserialize(using = TickerDeserializer.class)
public class Ticker {

    private Integer id;
    private String code;
    private String databaseCode;
    private String name;
    private String description;
    private LocalDate newestAvailableDate;
    private LocalDate oldestAvailableDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Integer> columnMap = new HashMap<>();
    private List<StockInfo> stockList = new ArrayList<>();
}
