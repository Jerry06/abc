package com.example.stock.service;

import com.example.stock.domain.Ticker;
import com.example.stock.domain.TickerAverage;
import com.example.stock.exception.InvalidTickerException;
import com.example.stock.exception.NoDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

import static java.util.stream.Collectors.toList;

/**
 * Created by vietnguyen on 23/06/2017.
 * The class handles business code of Ticker
 * This is using SpringCache with ehcache3 provider.
 */
@Service
public class TickerService {

    private TickerServiceCache tickerServiceCache;

//    @Bean
//    public TickerServiceCache tickerServiceCache() {
//        return new TickerServiceCache();
//    }

    @Autowired
    public TickerService(TickerServiceCache tickerServiceCache) {
        this.tickerServiceCache = tickerServiceCache;
    }

    /**
     * get Ticker information from Quandl and caching it
     *
     * @param ticketName : is ticker symbol
     * @return
     */
    public Ticker getTicker(String ticketName) {
        return tickerServiceCache.getTicker(ticketName);
    }

    /**
     * validation ticker is having moving average from the start date
     *
     * @param ticker
     * @param startDate : start date
     * @param dmaDays   : number of days moving average
     */
    public void validateData(Ticker ticker, LocalDate startDate, int dmaDays) {
        long count = ticker.getStockInfos().parallelStream()
                .filter(p -> !startDate.isAfter(p.getDate()))
                .count();
        if (count < dmaDays) {
            String suggestDate = "N/A";
            if (ticker.getStockInfos().size() > dmaDays) {
                suggestDate = ticker.getStockInfos().get(dmaDays - 1).getDate().toString();
            }
            throw new NoDataException(startDate, suggestDate);
        }
    }

    /**
     * get stock info list of ticker in range between start & end date
     *
     * @param startDate
     * @param endDate
     * @param ticker
     * @return
     */
    public List<List<String>> getStockInfoLists(LocalDate startDate, LocalDate endDate, Ticker ticker) {
        return ticker.getStockInfos().stream()
                .filter(p -> !startDate.isAfter(p.getDate())
                        && !endDate.isBefore(p.getDate()))
                .map(p -> p.toCloseInfo())
                .collect(toList());
    }

    /**
     * get average price of a ticker from start date within number of days moving average
     *
     * @param tickerSymbol
     * @param startDate
     * @param dmaDays      : number of days moving average
     * @return
     * @throws InvalidTickerException
     * @throws NoDataException
     */
    public TickerAverage getAverage(String tickerSymbol, LocalDate startDate, int dmaDays) throws InvalidTickerException, NoDataException {
        Ticker ticker = tickerServiceCache.getTicker(tickerSymbol);
        this.validateData(ticker, startDate, dmaDays);
        OptionalDouble average = ticker.getStockInfos().parallelStream()
                .filter(p -> !startDate.isAfter(p.getDate()))
                .limit(dmaDays)
                .mapToDouble(p -> p.getClosePrice()).average();
        if (average.isPresent()) {
            return new TickerAverage(tickerSymbol, average.getAsDouble());
        }
        throw new NoDataException(startDate, "N/A");
    }


    public static void main(String[] args) throws Exception {

//        String json = "{\n" +
//                "\t\"dataset\": {\n" +
//                "\t\t\"id\": 9775709,\n" +
//                "\t\t\"dataset_code\": \"GE\",\n" +
//                "\t\t\"database_code\": \"WIKI\",\n" +
//                "\t\t\"name\": \"General Electric Co (GE) Prices, Dividends, Splits and Trading Volume\",\n" +
//                "\t\t\"description\": \"End of day open, high, low, close and volume, dividends and splits, and split/dividend adjusted open, high, low close and volume for General Electric Company (GE). Ex-Dividend is non-zero on ex-dividend dates. Split Ratio is 1 on non-split dates. Adjusted prices are calculated per CRSP (www.crsp.com/products/documentation/crsp-calculations)\\n\\nThis data is in the public domain. You may copy, distribute, disseminate or include the data in other products for commercial and/or noncommercial purposes.\\n\\nThis data is part of Quandl's Wiki initiative to get financial data permanently into the public domain. Quandl relies on users like you to flag errors and provide data where data is wrong or missing. Get involved: connect@quandl.com\\n\",\n" +
//                "\t\t\"refreshed_at\": \"2017-06-22T21:47:09.604Z\",\n" +
//                "\t\t\"newest_available_date\": \"2017-06-22\",\n" +
//                "\t\t\"oldest_available_date\": \"1962-01-02\",\n" +
//                "\t\t\"column_names\": [\"Date\", \"Open\", \"High\", \"Low\", \"Close\", \"Volume\", \"Ex-Dividend\", \"Split Ratio\", \"Adj. Open\", \"Adj. High\", \"Adj. Low\", \"Adj. Close\", \"Adj. Volume\"],\n" +
//                "\t\t\"frequency\": \"daily\",\n" +
//                "\t\t\"type\": \"Time Series\",\n" +
//                "\t\t\"premium\": false,\n" +
//                "\t\t\"limit\": null,\n" +
//                "\t\t\"transform\": null,\n" +
//                "\t\t\"column_index\": null,\n" +
//                "\t\t\"start_date\": \"1962-01-02\",\n" +
//                "\t\t\"end_date\": \"2017-06-22\",\n" +
//                "\t\t\"data\": [\n" +
//                "\t\t\t[\"2017-06-22\", 27.77, 27.83, 27.54, 27.55, 27013118.0, 0.0, 1.0, 27.77, 27.83, 27.54, 27.55, 27013118.0],\n" +
//                "\t\t\t[\"2017-06-21\", 28.18, 28.19, 27.645, 27.78, 33568459.0, 0.0, 1.0, 28.18, 28.19, 27.645, 27.78, 33568459.0],\n" +
//                "\t\t\t[\"2017-06-20\", 28.71, 28.72, 28.08, 28.13, 47389248.0, 0.0, 1.0, 28.71, 28.72, 28.08, 28.13, 47389248.0],\t\t\t\n" +
//                "\t\t\t[\"1962-01-02\", 75.0, 76.25, 74.25, 74.75, 21600.0, 0.0, 1.0, 0.3328099987939, 0.33835683210713, 0.32948189880596, 0.33170063213126, 2073600.0]\n" +
//                "\t\t],\n" +
//                "\t\t\"collapse\": null,\n" +
//                "\t\t\"order\": null,\n" +
//                "\t\t\"database_id\": 4922\n" +
//                "\t}\n" +
//                "}";
//        ObjectMapper mapper = new ObjectMapper();
//        Ticker ticker = mapper.readValue(json, Ticker.class);
//        System.out.println(ticker);
//        JsonNode node = mapper.readValue(json, JsonNode.class);
//        JsonNode jsonNode = node.get(0);
//        System.out.println(node);
//        Ticker ge = new StockService().getTicker("AB");
//        System.out.println(ge.getId());
//        ge = new StockService().getTicker("GE");
//        System.out.println(ge.getId());
    }
}
