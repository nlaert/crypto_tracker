package pt.laert.tracker.error;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String symbol) {
        super("Asset not found: " + symbol);
    }
}
