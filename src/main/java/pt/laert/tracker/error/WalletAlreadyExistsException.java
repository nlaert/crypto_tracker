package pt.laert.tracker.error;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException() {
        super("A wallet already exists for this email");
    }
}
