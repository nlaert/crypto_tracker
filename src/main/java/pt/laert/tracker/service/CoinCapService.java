package pt.laert.tracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.error.AssetNotFoundException;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;

@Service
public class CoinCapService {
    @Value(value = "${spring.coincap.search-url}")
    private String API_URL;
    private final int NUMBER_OF_THREADS = 3;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    public CoinCapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public CoinData searchForAsset(String symbol) throws AssetNotFoundException {
        String url = API_URL + symbol;
        Data response = restTemplate.getForObject(url, Data.class);
        if (response == null) {
            throw new AssetNotFoundException(symbol);
        } else {
            return response.getData().stream().filter(coinData ->
                            coinData.getSymbol().equals(symbol))
                    .findFirst().orElse(null);
        }
    }

    private Asset getAssetPrice(String symbol) {
        CoinData coinData = searchForAsset(symbol);
        if (coinData == null) {
            throw new AssetNotFoundException(symbol);
        } else {
            var asset = new Asset();
            asset.setPrice(coinData.getPriceAsDouble());
            asset.setSymbol(symbol);
            return asset;
        }
    }

    public List<Asset> getAssetsPrices(List<String> symbols) {
        List<Asset> assets = new ArrayList<>();
        for (int i = 0; i < symbols.size(); i += NUMBER_OF_THREADS) {
            int maxIndex = Math.min(i + NUMBER_OF_THREADS, symbols.size());
            var subList = symbols.subList(i, maxIndex);
            var future = CompletableFuture.runAsync(() ->
                subList.forEach(symbol -> assets.add(getAssetPrice(symbol))), executorService);
            future.join();
        }
        return assets;
    }
}

