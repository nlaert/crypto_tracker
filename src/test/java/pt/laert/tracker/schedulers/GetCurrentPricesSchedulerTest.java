package pt.laert.tracker.schedulers;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.scheduler.GetCurrentPricesScheduler;
import pt.laert.tracker.service.CoinCapService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentPricesSchedulerTest {

    @Mock
    private AssetsRepository assetsRepository;

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private GetCurrentPricesScheduler getCurrentPricesScheduler;

    @Test
    public void testFetchAssets() {
        // Given
        var assets = createAssetEntities();
        var coinData = createCoinData();

        // When
        when(assetsRepository.findAll()).thenReturn(assets);

        when(coinCapService.searchForAsset("BTC")).thenReturn(coinData.get(0));
        when(coinCapService.searchForAsset("ETH")).thenReturn(coinData.get(1));
        when(coinCapService.searchForAsset("XRP")).thenReturn(coinData.get(2));
        when(coinCapService.searchForAsset("ADA")).thenReturn(coinData.get(3));

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

    private List<CoinData> createCoinData() {
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
        return List.of(btc, eth, xrp, ada);
    }

}