
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;

  public TiingoService(RestTemplate restTemplate) {
	this.restTemplate = restTemplate;
}

@Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
      ObjectMapper om=new ObjectMapper();
      om.registerModule(new JavaTimeModule());
      String url= buildUri(symbol, from, to);
     // System.out.println("Generated URL: " + url); 
    //  return Arrays.asList(restTemplate.getForObject(url, TiingoCandle[].class));
    String candles = restTemplate.getForObject(url, String.class);
    List<Candle> lst=Arrays.asList(om.readValue(candles, TiingoCandle[].class));

    return lst;

    
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?"
    + "startDate=" + startDate + "&endDate=" + endDate + "&token=" + PortfolioManagerApplication.getToken();
return uriTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
