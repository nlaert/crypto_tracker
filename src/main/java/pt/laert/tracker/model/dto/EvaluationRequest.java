package pt.laert.tracker.model.dto;

import java.util.List;

public class EvaluationRequest {
    private List<Asset> assets;

    public EvaluationRequest(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}
