package com.example.stock.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Tho on 25/06/2017.
 */
@RestController
public class HelloController {
    @RequestMapping("/")
    public String gethelloWorld() {
        return "Hello World!";
    }
}
