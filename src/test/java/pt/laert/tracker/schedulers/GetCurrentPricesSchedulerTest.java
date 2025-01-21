package pt.laert.tracker.schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.scheduler.GetCurrentPricesScheduler;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentPricesSchedulerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExecutorService executorService;

    @Mock
    private AssetsRepository assetsRepository;

    @InjectMocks
    private GetCurrentPricesScheduler getCurrentPricesScheduler;

    @Test
    public void testFetchAssets() {
        // Given
        var assets = createAssetEntities();
        var coinData = createCoinData();
        var url = "https://api.coincap.io/v2/assets?search=";

        // When
        when(assetsRepository.findAll()).thenReturn(assets);

        when(restTemplate.getForObject(eq(url + "BTC"), eq(Data.class))).thenReturn(coinData);
        when(restTemplate.getForObject(eq(url + "ETH"), eq(Data.class))).thenReturn(coinData);
        when(restTemplate.getForObject(eq(url + "XRP"), eq(Data.class))).thenReturn(coinData);
        when(restTemplate.getForObject(eq(url + "ADA"), eq(Data.class))).thenReturn(coinData);

        getCurrentPricesScheduler.getCurrentPrices();

        // Then
        verify(assetsRepository).save(assets.get(0));
        verify(assetsRepository).save(assets.get(1));
        verify(assetsRepository).save(assets.get(2));
        verify(assetsRepository).save(assets.get(3));
    }

    private List<AssetEntity> createAssetEntities() {
        var btc = new AssetEntity(
                "BTC",
                "Bitcoin",
                100000.00
        );
        var eth = new AssetEntity(
                "ETH",
                "Ethereum",
                3005.21
        );
        var xrp = new AssetEntity(
                "XRP",
                "Ripple",
                3.19
        );
        var ada = new AssetEntity(
                "ADA",
                "Cardano",
                1.01
        );
        return List.of(btc, eth, xrp, ada);
    }

    private Data createCoinData() {
        var btc = new CoinData(
                "bitcoin",
                "BTC",
                "Bitcoin",
                BigDecimal.valueOf(100000.00)
        );
        var eth = new CoinData(
                "ethereum",
                "ETH",
                "Ethereum",
                BigDecimal.valueOf(3005.21)
        );
        var xrp = new CoinData(
                "ripple",
                "XRP",
                "Ripple",
                BigDecimal.valueOf(3.19)
        );
        var ada = new CoinData(
                "cardano",
                "ADA",
                "Cardano",
                BigDecimal.valueOf(1.01)
        );
        return new Data(List.of(btc, eth, xrp, ada));
    }

}