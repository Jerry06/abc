package com.example.stock.exception;

import java.time.LocalDate;

/**
 * Created by vietnguyen on 24/06/2017.
 */
public class NoDataException extends RuntimeException {

    public NoDataException(LocalDate startDate, String suggestDate) {
        super(String.format("There is no data for the StartDate = %s. Posisible Date : %s", startDate, suggestDate));
    }
}