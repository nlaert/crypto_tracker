package pt.laert.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet_assets")
@IdClass(WalletAssetId.class)
public class WalletAssetsEntity {
    @Id
    @Column(name = "wallet_id")
    private Long walletId;

    @Id
    @Column(name = "symbol")
    private String symbol;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "original_value")
    private double originalValue;

    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", insertable = false, updatable = false)
    private WalletEntity wallet;

    public WalletAssetsEntity(Long walletId, String symbol, BigDecimal quantity, double originalValue) {
        this.walletId = walletId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.originalValue = originalValue;
    }

    public WalletAssetsEntity() {
        // No-arg constructor for JPA
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
    }

    public WalletEntity getWallet() {
        return wallet;
    }
}
