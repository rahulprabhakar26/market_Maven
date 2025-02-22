
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.net.URI;
import java.net.URISyntaxException;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class PortfolioManagerImpl implements PortfolioManager {

 private RestTemplate restTemplate ;
 
 private StockQuotesService stockQuotesService;

 protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
  this.stockQuotesService= stockQuotesService;
}


// public List<Candle> getStockQuote(String symbol, LocalDate startLocalDate, LocalDate endLocalDate)
//       throws JsonProcessingException {
//         return stockQuotesService.getStockQuote(symbol, startLocalDate, endLocalDate);
//       }
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
   // this.stockQuotesService= stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
    //   String url= buildUri(symbol, from, to);
    //  // System.out.println("Generated URL: " + url); 
    //  return Arrays.asList(restTemplate.getForObject(url, TiingoCandle[].class));

    return stockQuotesService.getStockQuote(symbol, from, to);
    
  }
//   public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
//       throws JsonProcessingException {
//     String url = "https://api.tiingo.com/tiingo/daily/AAPL/prices?startDate=2020-01-01&endDate=2020-12-31&token=1106a1a3940ab609f2cac6fc9630caee9c6dfc58";
//     URI uri;
//     try {
//         uri = new URI(url);
//         System.out.println("URI: " + uri.toString());
//     } catch (URISyntaxException e) {
//         e.printStackTrace();
//         throw new RuntimeException("Invalid URL", e);
//     }
//     return Arrays.asList(restTemplate.getForObject(uri, TiingoCandle[].class));
// }



  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?"
    + "startDate=" + startDate + "&endDate=" + endDate + "&token=" + PortfolioManagerApplication.getToken();
return uriTemplate;
  }
// protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
//   return UriComponentsBuilder.fromHttpUrl("https://api.tiingo.com/tiingo/daily/" + symbol + "/prices")
//           .queryParam("startDate", startDate)
//           .queryParam("endDate", endDate)
//           .queryParam("token", PortfolioManagerApplication.getToken())
//           .toUriString();
// }



  


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws JsonProcessingException, StockQuoteServiceException {

           List<AnnualizedReturn>annualizedReturns = new ArrayList <AnnualizedReturn>() ;

        for(PortfolioTrade  portfolioTrade : portfolioTrades)
        {    
               // Fetch the list of candles for the given trade and end date
        List<Candle> result = getStockQuote(portfolioTrade.getSymbol(),portfolioTrade.getPurchaseDate(), endDate);

        // Calculate the buy price and sell price
        double buyPrice = result.get(0).getOpen();
        double sellPrice = result.get(result.size() - 1).getClose();
        // Calculate the annualized return for this trade
        AnnualizedReturn annualizedReturn = PortfolioManagerApplication.calculateAnnualizedReturns(endDate, portfolioTrade, buyPrice, sellPrice);
        
        // Add the calculated return to the list
        annualizedReturns.add(annualizedReturn);
            
        }
     Collections.sort(annualizedReturns, getComparator());
    return annualizedReturns;
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
