package pt.laert.tracker.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(
            @RequestBody() Wallet wallet
    ) {
        var newWallet = walletService.createWallet(wallet.getEmail());
        return ResponseEntity.created(URI.create("/wallet/" + newWallet.getWalletId())).body(newWallet);
    }

}
