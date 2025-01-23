package pt.laert.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.error.AssetNotFoundException;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;

@Service
public class CoinCapService {
    @Value(value = "${spring.coincap.search-url}")
    private String API_URL;
    private final RestTemplate restTemplate;

    public CoinCapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CoinData searchForAsset(String symbol) {
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

    public double getAssetPrice(String symbol) {
        CoinData coinData = searchForAsset(symbol);
        if (coinData == null) {
            throw new AssetNotFoundException(symbol);
        } else {
            return coinData.getPriceAsDouble();
        }
    }
}
