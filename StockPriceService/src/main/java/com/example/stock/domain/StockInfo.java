package com.example.stock.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vietnguyen on 23/06/2017.
 */
@Data
public class StockInfo {

    private LocalDate date;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;

    public List<String> toCloseInfo() {
        return Arrays.asList(date.toString(), closePrice.toString());
    }

}
