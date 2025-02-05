package pt.laert.tracker.model.dto;

import java.math.BigDecimal;

public class Asset {
    private String symbol;
    private BigDecimal quantity;
    private Double price;
    private Double value;

    public Asset() {
    }

    public Asset(String symbol, BigDecimal quantity, Double price) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        if (value != null) {
            return value;
        }
        return price * quantity.doubleValue();
    }
}
