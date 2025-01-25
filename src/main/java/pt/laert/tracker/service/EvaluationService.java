package pt.laert.tracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.EvaluationRequest;
import pt.laert.tracker.model.dto.EvaluationResponse;

@Service
public class EvaluationService {
    private final CoinCapService coinCapService;

    public EvaluationService(CoinCapService coinCapService) {
        this.coinCapService = coinCapService;
    }

    public EvaluationResponse evaluateAssets(EvaluationRequest evaluationRequest) {
        var currentPrices = getCurrentPrices(evaluationRequest);
        var originalPrices = calculateOriginalPricesOfAssets(evaluationRequest);
        var performance = calculatePerformance(currentPrices, originalPrices);
        performance.sort(Comparator.comparing(Asset::getValue).reversed());

        return new EvaluationResponse(
                round(calculateTotal(performance)),
                performance.get(0).getSymbol(),
                round(performance.get(0).getValue()),
                performance.get(performance.size() - 1).getSymbol(),
                round(performance.get(performance.size() - 1).getValue())
        );
    }

    private List<Asset> getCurrentPrices(EvaluationRequest evaluationRequest) {
        var symbols = evaluationRequest.getAssets().stream().map(Asset::getSymbol).toList();
        return coinCapService.getAssetsPrices(symbols);
    }

    private List<Asset> calculateOriginalPricesOfAssets(EvaluationRequest evaluationRequest) {
        return evaluationRequest.getAssets().stream().map(asset ->
                new Asset(
                        asset.getSymbol(),
                        asset.getQuantity(),
                        asset.getValue() / asset.getQuantity().doubleValue()
                )
        ).toList();
    }

    private ArrayList<Asset> calculatePerformance(List<Asset> currentPrices, List<Asset> originalPrices) {
        return currentPrices.stream().map(currentPrice -> {
            var originalPrice = originalPrices.stream().filter(asset ->
                    asset.getSymbol().equals(currentPrice.getSymbol())).findFirst().get();
            var asset = new Asset();
            asset.setSymbol(currentPrice.getSymbol());
            asset.setQuantity(originalPrice.getQuantity());
            asset.setPrice(currentPrice.getPrice());
            asset.setValue((currentPrice.getPrice() - originalPrice.getPrice()) / originalPrice.getPrice() * 100);
            return asset;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    private Double calculateTotal(List<Asset> assets) {
        return assets.stream().mapToDouble(asset -> asset.getQuantity().doubleValue() * asset.getPrice()).sum();
    }

    private Double round(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
