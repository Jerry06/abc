package com.example.stock;

import com.example.stock.domain.DMA200;
import com.example.stock.domain.DMAs200;
import com.example.stock.domain.Ticker;
import com.example.stock.domain.TickerPrices;
import com.example.stock.exception.InvalidTickerException;
import com.example.stock.service.TickerService;
import com.example.stock.service.TickerServiceCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockPriceServiceApplicationTests {

    @MockBean
    private TickerServiceCache tickerServiceCache;

    @Autowired
    private TickerService tickerService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setup() throws Exception {
        initTicker("GE");
        initTicker("FB");
        initTicker("FIS");
        when(tickerServiceCache.getTicker("AC")).thenThrow(new InvalidTickerException("AC"));
    }

    private void initTicker(String tickerSymbol) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(tickerSymbol + ".json");
        Ticker ticker = mapper.readValue(is, Ticker.class);
        when(tickerServiceCache.getTicker(tickerSymbol)).thenReturn(ticker);
    }

    @Test
    public void testTickerDeserializer(){
        Ticker ge = tickerServiceCache.getTicker("GE");
        assertThat(ge).isNotNull();
        assertThat(ge.getId()).isEqualTo(9775709);
        assertThat(ge.getCode()).isEqualTo("GE");
        assertThat(ge.getDatabaseCode()).isEqualTo("WIKI");
        assertThat(ge.getName()).isEqualTo("General Electric Co (GE) Prices, Dividends, Splits and Trading Volume");
        assertThat(ge.getDescription()).isEqualTo("GE description");
    }

    @Test
    public void testCache() {
        TickerServiceCache mock = Mockito.mock(TickerServiceCache.class);
        Ticker obj1 = new Ticker();
        Ticker obj2 = new Ticker();
        when(mock.getTicker(anyString())).thenReturn(obj1, obj2);
        Ticker ge = mock.getTicker("GE");
        assertThat(ge).isEqualTo(obj1);
        ge = mock.getTicker("GE");
        assertThat(ge).isEqualTo(obj1);
    }

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
        assertThat(tickerPrices.getClosePriceList().get(0).getCloseInfoList()).contains(Arrays.asList("2017-06-22", "27.55"));
        assertThat(tickerPrices.getClosePriceList().get(0).getCloseInfoList()).contains(Arrays.asList("2017-06-21", "27.78"));
        assertThat(tickerPrices.getClosePriceList().get(0).getCloseInfoList()).contains(Arrays.asList("2017-06-20", "28.13"));
    }

    @Test
    public void testGetTickerInvalidDateRange() throws Exception {
        String startDate = "2017-06-20";
        String endDate = "2017-06-19";
        String url = String.format("/api/v2/GE/closePrice?startDate=%s&endDate=%s", startDate, endDate);
        ResponseEntity<TickerPrices> responseEntity = testRestTemplate.getForEntity(url, TickerPrices.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetTickerInvalidTicker() throws Exception {
        String url = String.format("/api/v2/AC/closePrice?startDate=2016-06-20&endDate=2016-06-20");
        ResponseEntity<TickerPrices> responseEntity = testRestTemplate.getForEntity(url, TickerPrices.class);
        assertThat(responseEntity).isNotNull();
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

    @Test
    public void testGetTickerAverageNoData() {
        String startDate = "2017-4-20";
        String url = String.format("/api/v2/FIS/200dma?startDate=%s", startDate);
        ResponseEntity<DMA200> responseEntity = testRestTemplate.getForEntity(url, DMA200.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetMultiTickerAverageSuccess() {
        String startDate = "2016-06-20";
        String tickerSymbols = "GE,FIS,FB";
        String url = String.format("/api/v2/200dma?startDate=%s&tickerSymbols=%s", startDate, tickerSymbols);
        DMAs200 result = testRestTemplate.getForObject(url, DMAs200.class);
        assertThat(result).isNotNull();
        assertThat(result.getDma200()).isNotNull();
        assertThat(result.getNumberSuccess()).isEqualTo(3);
        assertThat(result.getDma200().size()).isEqualTo(3);
        assertThat(result.getDma200()).extracting("tickerName", "avg").contains(tuple("GE", 29.79725));
        assertThat(result.getDma200()).extracting("tickerName", "avg").contains(tuple("FIS", 79.71619999999999));
        assertThat(result.getDma200()).extracting("tickerName", "avg").contains(tuple("FB", 134.07920000000001));
    }

    @Test
    public void testGetMultiTickerAveragePartialSuccess() {
        String startDate = "2016-06-20";
        String tickerSymbols = "GE,FIS,AC";
        String url = String.format("/api/v2/200dma?startDate=%s&tickerSymbols=%s", startDate, tickerSymbols);
        DMAs200 result = testRestTemplate.getForObject(url, DMAs200.class);
        assertThat(result).isNotNull();
        assertThat(result.getDma200()).isNotNull();
        assertThat(result.getNumberSuccess()).isEqualTo(2);
        assertThat(result.getNumberFail()).isEqualTo(1);
        assertThat(result.getDma200().size()).isEqualTo(2);
        assertThat(result.getDma200()).extracting("tickerName", "avg").contains(tuple("GE", 29.79725));
        assertThat(result.getDma200()).extracting("tickerName", "avg").contains(tuple("FIS", 79.71619999999999));
        assertThat(result.getErrors().size()).isEqualTo(1);
        assertThat(result.getErrors()).extracting("tickerName").contains("AC");
    }

}
