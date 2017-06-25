package com.example.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StockPriceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockPriceServiceApplication.class, args);
	}
}
