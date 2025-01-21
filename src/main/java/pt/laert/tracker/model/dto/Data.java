package pt.laert.tracker.model.dto;

import java.util.List;

public class Data {
    private List<CoinData> data;

    public Data(List<CoinData> data) {
        this.data = data;
    }

    public List<CoinData> getData() {
        return data;
    }

    public void setData(List<CoinData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Data{" +
                "data=" + data +
                '}';
    }
}
