package com.example.stock.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by vietnguyen on 24/06/2017.
 */
public class TickerDeserializer extends JsonDeserializer<Ticker> {

    @Override
    public Ticker deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode rootNode = jsonNode.get(Constants.ROOT_ELEMENT);
        Ticker ticker = new Ticker();
        ticker.setId(asInt(rootNode.get(Constants.TICKER_ID)));
        ticker.setName(asText(rootNode.get(Constants.TICKER_NAME)));
        ticker.setDescription(asText(rootNode.get(Constants.TICKER_DESCRIPTION)));
        ticker.setCode(asText(rootNode.get(Constants.TICKER_CODE)));
        ticker.setDatabaseCode(asText(rootNode.get(Constants.TICKER_DATABASE_CODE)));
        ticker.setStartDate(asLocalDate(rootNode.get(Constants.TICKER_START_DATE)));
        ticker.setEndDate(asLocalDate(rootNode.get(Constants.TICKER_END_DATE)));
        ticker.setNewestAvailableDate(asLocalDate(rootNode.get(Constants.TICKER_NEWEST_DATE)));
        ticker.setOldestAvailableDate(asLocalDate(rootNode.get(Constants.TICKER_OLDEST_DATE)));
        JsonNode columnNames = rootNode.get(Constants.TICKER_COLUMN_NAMES);
        JsonNode data = rootNode.get(Constants.TICKER_DATA);
        if (columnNames != null && data != null) {
            int index = 0;
            if (columnNames.isArray()) {
                for (JsonNode objNode : columnNames) {
                    ticker.getColumnMap().put(asText(objNode), index++);
                }
            }
            if (data.isArray()) {
                for (JsonNode arrNode : data) {
                    StockInfo info = new StockInfo();
                    info.setDate(asLocalDate(arrNode.get(ticker.getColumnMap().get(Constants.STOCK_DATE))));
                    info.setOpenPrice(asDouble(arrNode.get(ticker.getColumnMap().get(Constants.STOCK_OPEN))));
                    info.setClosePrice(asDouble(arrNode.get(ticker.getColumnMap().get(Constants.STOCK_CLOSE))));
                    info.setHighPrice(asDouble(arrNode.get(ticker.getColumnMap().get(Constants.STOCK_HIGH))));
                    info.setLowPrice(asDouble(arrNode.get(ticker.getColumnMap().get(Constants.STOCK_LOW))));
                    ticker.getStockInfos().add(info);
                }
            }
        }

        return ticker;
    }


    private LocalDate asLocalDate(JsonNode jsonNode) {
        try {
            return LocalDate.parse(asText(jsonNode), DateTimeFormatter.ofPattern(Constants.TICKER_DATE_FORMAT));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Double asDouble(JsonNode jsonNode) {
        return jsonNode != null ? jsonNode.asDouble() : 0;
    }

    private String asText(JsonNode jsonNode) {
        return jsonNode != null ? jsonNode.asText() : "";
    }

    private Integer asInt(JsonNode jsonNode) {
        return jsonNode != null ? jsonNode.asInt() : 0;
    }
}
