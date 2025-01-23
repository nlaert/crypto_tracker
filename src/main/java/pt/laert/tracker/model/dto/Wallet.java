package pt.laert.tracker.model.dto;

public class Wallet {
    private Long walletId;
    private String email;

    public Wallet() {
    }

    public Wallet(Long walletId, String email) {
        this.walletId = walletId;
        this.email = email;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
