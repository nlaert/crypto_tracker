package pt.laert.tracker.service;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.error.AssetNotFoundException;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinCapServiceTest {

    @InjectMocks
    private CoinCapService coinCapService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${spring.coincap.search-url}")
    private String API_URL;

    @Test
    void testSearchForAsset_NullResponse() {
        String symbol = "BTC";
        when(restTemplate.getForObject(API_URL + symbol, Data.class)).thenReturn(null);

        Exception exception = assertThrows(AssetNotFoundException.class, () -> {
            coinCapService.searchForAsset(symbol);
        });
        assertEquals("Asset not found: " + symbol, exception.getMessage());
    }

    @Test
    void testSearchForAsset_AssetNotFoundInData() {
        String symbol = "BTC";
        Data response = new Data(List.of(new CoinData("ethereum", "ETH", "Ethereum", BigDecimal.valueOf(3000.0))));

        when(restTemplate.getForObject(API_URL + symbol, Data.class)).thenReturn(response);

        CoinData result = coinCapService.searchForAsset(symbol);
        assertNull(result);
    }

    @Test
    void testSearchForAsset_Success() {
        String symbol = "BTC";
        CoinData expectedCoinData = new CoinData("bitcoin", symbol, "Bitcoin", BigDecimal.valueOf(50000.0));
        Data response = new Data(List.of(expectedCoinData, new CoinData("ethereum", "ETH", "Ethereum", BigDecimal.valueOf(3000.0))));

        when(restTemplate.getForObject(API_URL + symbol, Data.class)).thenReturn(response);

        CoinData result = coinCapService.searchForAsset(symbol);
        assertNotNull(result);
        assertEquals(expectedCoinData.getSymbol(), result.getSymbol());
        assertEquals(expectedCoinData.getName(), result.getName());
        assertEquals(expectedCoinData.getPriceAsDouble(), result.getPriceAsDouble());
    }
}