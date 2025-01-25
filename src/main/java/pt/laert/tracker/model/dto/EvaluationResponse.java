package pt.laert.tracker.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class EvaluationResponse {
    private Double total;
    private String bestAsset;
    private Double bestPerformance;
    private String worstAsset;
    private Double worstPerformance;

    public EvaluationResponse(Double total, String bestAsset, Double bestPerformance, String worstAsset, Double worstPerformance) {
        this.total = total;
        this.bestAsset = bestAsset;
        this.bestPerformance = bestPerformance;
        this.worstAsset = worstAsset;
        this.worstPerformance = worstPerformance;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getBestAsset() {
        return bestAsset;
    }

    public void setBestAsset(String bestAsset) {
        this.bestAsset = bestAsset;
    }

    public Double getBestPerformance() {
        return bestPerformance;
    }

    public void setBestPerformance(Double bestPerformance) {
        this.bestPerformance = bestPerformance;
    }

    public String getWorstAsset() {
        return worstAsset;
    }

    public void setWorstAsset(String worstAsset) {
        this.worstAsset = worstAsset;
    }

    public Double getWorstPerformance() {
        return worstPerformance;
    }

    public void setWorstPerformance(Double worstPerformance) {
        this.worstPerformance = worstPerformance;
    }
}
