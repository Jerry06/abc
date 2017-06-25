package com.example.stock;

import com.example.stock.domain.DMA200;
import com.example.stock.domain.Ticker;
import com.example.stock.domain.TickerPrices;
import com.example.stock.service.TickerServiceCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(TickerController.class)
public class StockPriceServiceApplicationTests {

    @MockBean
    private TickerServiceCache tickerServiceCache;

    @Autowired
    private TestRestTemplate testRestTemplate;

//    @TestConfiguration
//    static class Config {
//
//        @Bean
//        public TestRestTemplate restTemplateBuilder() {
//            return new TestRestTemplate();
//        }
//
//    }

//    @Autowired
//    private TickerService tickerService;
//
//    private TickerService tickerService2;

//    private MockMvc mockMvc;

    private Ticker geTicker;

    @Before
    public void setup() throws Exception {
//        tickerService = new TickerService(tickerServiceCache);
//        mockMvc = MockMvcBuilders.standaloneSetup(new TickerController(tickerService)).build();
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("GE.json");
        geTicker = mapper.readValue(is, Ticker.class);
        when(tickerServiceCache.getTicker("GE")).thenReturn(geTicker);
    }


//    @Test
//    public void validateCache() {
//        Ticker ticker = new Ticker();
//        when(tickerService.getTicker("GE")).thenReturn(ticker);
//        Cache stockCache = this.cacheManager.getCache("stockCache");
//        assertThat(stockCache).isNotNull();
//        stockCache.clear();
//        assertThat(stockCache.get("GE")).isNull();
//        Ticker ge = tickerService.getTicker("GE");
//        assertEquals(ticker, ge);
//        assertThat((Ticker) stockCache.get("GE").get()).isEqualTo(ticker);
//    }

    @Test
    public void testGetTickerClosePrice() throws Exception {
        String startDate = "2017-06-20";
        String endDate = "2017-06-23";
        String url = String.format("/api/v2/GE/closePrice?startDate=%s&endDate=%s", startDate, endDate);
        TickerPrices tickerPrices = testRestTemplate.getForObject(url, TickerPrices.class);
        assertThat(tickerPrices).isNotNull();
        assertThat(tickerPrices.getClosePriceList().size()).isEqualTo(1);
        assertThat(tickerPrices.getClosePriceList().get(0).getTickerName()).isEqualTo("GE");
        assertThat(tickerPrices.getClosePriceList().get(0).getCloseInfoList().size()).isEqualTo(3);
    }

    @Test
    public void testGetTickerInvalidDateRange() throws Exception {
        String startDate = "2017-06-20";
        String endDate = "2017-06-19";
        String url = String.format("/api/v2/GE/closePrice?startDate=%s&endDate=%s", startDate, endDate);
        ResponseEntity<TickerPrices> responseEntity = testRestTemplate.getForEntity(url, TickerPrices.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetTickerAverage() throws Exception {
        String startDate = "2016-06-20";
        String url = String.format("/api/v2/GE/200dma?startDate=%s", startDate);
        DMA200 result = testRestTemplate.getForObject(url, DMA200.class);
        assertThat(result).isNotNull();
        assertThat(result.getDma200()).isNotNull();
        assertThat(result.getDma200().getTickerName()).isEqualTo("GE");
        assertThat(result.getDma200().getAvg()).isEqualTo(29.79725);
    }


}
