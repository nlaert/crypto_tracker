package pt.laert.tracker.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ResponseEntity<?> handleWalletAlreadyExistsException(WalletAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
