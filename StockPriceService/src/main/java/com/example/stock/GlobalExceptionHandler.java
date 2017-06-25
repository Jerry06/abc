package com.example.stock;

import com.example.stock.exception.InvalidDateRangeException;
import com.example.stock.exception.InvalidTickerException;
import com.example.stock.exception.NoDataException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by vietnguyen on 24/06/2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {
            InvalidDateRangeException.class,
            InvalidTickerException.class,
            NoDataException.class})
    public Exception handleBaseException(Exception e) {
        return e;
    }
}