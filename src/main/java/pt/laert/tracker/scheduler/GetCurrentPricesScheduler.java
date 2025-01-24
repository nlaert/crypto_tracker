package pt.laert.tracker.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;
import pt.laert.tracker.repositories.AssetsRepository;

@Service
public class GetCurrentPricesScheduler {

    private static final int NUMBER_OF_THREADS = 3;

    private final RestTemplate restTemplate;

    private final AssetsRepository assetsRepository;

    private final ExecutorService executorService;

    @Autowired
    public GetCurrentPricesScheduler(RestTemplate restTemplate, AssetsRepository assetsRepository) {
        this.restTemplate = restTemplate;
        this.assetsRepository = assetsRepository;
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    private final Logger logger = LoggerFactory.getLogger(GetCurrentPricesScheduler.class);

    @Scheduled(fixedRateString = "${get-prices.interval-in-millis}", initialDelay = 10000)
    public void getCurrentPrices() {
        logger.info("Starting to get current prices");
        var assets = assetsRepository.findAll();
        for (int i = 0; i < assets.size(); i += NUMBER_OF_THREADS) {
            int maxIndex = Math.min(i + NUMBER_OF_THREADS, assets.size());
            var subList = assets.subList(i, maxIndex);
            var future = CompletableFuture.runAsync(() ->
                            subList.forEach(this::getAssetData),
                    executorService);
            future.join();
        }
    }

    private void getAssetData(AssetEntity assetEntity) { // TODO: Replace this with coinCapService
        String url = "https://api.coincap.io/v2/assets?search=" + assetEntity.getSymbol();
        Data response = restTemplate.getForObject(url, Data.class);
        if (response != null) {
            logger.info("Got price for asset: {}.  Response: {}", assetEntity.getSymbol(), response);
            CoinData responseCoinData = response.getData().stream().filter(coinData ->
                            coinData.getSymbol().equals(assetEntity.getSymbol()))
                    .findFirst().orElse(null);
            if (responseCoinData != null) {
                assetEntity.setPrice(responseCoinData.getPriceAsDouble());
                assetsRepository.save(assetEntity);
            } else {
                logger.warn("Coincap search did not return expected symbol {}", assetEntity.getSymbol());
            }
        } else {
            logger.error("Error getting price for coin with symbol: {}", assetEntity.getSymbol());
        }
    }
}
