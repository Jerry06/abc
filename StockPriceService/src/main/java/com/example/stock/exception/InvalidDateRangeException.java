package com.example.stock.exception;

import java.time.LocalDate;

/**
 * Created by vietnguyen on 24/06/2017.
 */
public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super(String.format("StartDate = %s, EndDate = %s is invalid range.", startDate, endDate));
    }
}
