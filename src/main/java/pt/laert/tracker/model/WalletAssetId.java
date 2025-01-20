package pt.laert.tracker.model;

import java.io.Serializable;
import java.util.Objects;

public class WalletAssetId implements Serializable {
    private Long walletId;
    private String symbol;

    public WalletAssetId(Long walletId, String assetId) {
        this.walletId = walletId;
        this.symbol = assetId;
    }

    public WalletAssetId() {
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WalletAssetId that = (WalletAssetId) o;
        return Objects.equals(walletId, that.walletId) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, symbol);
    }
}
