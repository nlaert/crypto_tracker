package pt.laert.tracker.model.dto;

import java.util.List;
import java.util.Objects;

public class WalletAssets {
    private Long id;
    private Double total;
    private List<Asset> assets;

    public WalletAssets(Long id, Double total, List<Asset> assets) {
        this.id = id;
        this.total = total;
        this.assets = assets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WalletAssets that = (WalletAssets) o;
        return Objects.equals(id, that.id) && Objects.equals(total, that.total) && Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, total, assets);
    }
}
