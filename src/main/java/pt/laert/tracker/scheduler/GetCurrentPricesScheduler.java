package pt.laert.tracker.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.laert.tracker.error.AssetNotFoundException;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.service.CoinCapService;

@Service
public class GetCurrentPricesScheduler {

    private static final int NUMBER_OF_THREADS = 3;

    private final AssetsRepository assetsRepository;

    private final CoinCapService coinCapService;

    private final ExecutorService executorService;

    @Autowired
    public GetCurrentPricesScheduler(AssetsRepository assetsRepository, CoinCapService coinCapService) {
        this.assetsRepository = assetsRepository;
        this.coinCapService = coinCapService;
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

    private void getAssetData(AssetEntity assetEntity) {
        try {
            var response = coinCapService.searchForAsset(assetEntity.getSymbol());
            if (response != null) {
                logger.info("Got price for asset: {}.  Response: {}", assetEntity.getSymbol(), response);
                assetEntity.setPrice(response.getPriceAsDouble());
                assetsRepository.save(assetEntity);
            } else {
                logger.warn("Coincap search did not return expected symbol {}", assetEntity.getSymbol());
            }
        } catch (AssetNotFoundException exception) {
            logger.error("Asset not found: {}. This should be a temporary error.", assetEntity.getSymbol());
        }
    }
}
