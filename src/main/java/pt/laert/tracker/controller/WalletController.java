package pt.laert.tracker.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.model.dto.WalletAssets;
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

    @PutMapping("/{id}/asset")
    public ResponseEntity<Wallet> addAssetToWallet(
            @PathVariable Long id,
            @RequestBody() Asset asset
    ) {
        walletService.addAssetToWallet(id, asset);
        return ResponseEntity.noContent().build(); // TODO: replace by the getWallet
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletAssets> getWallet(
            @PathVariable Long id
    ) {
        var wallet = walletService.getWallet(id);
        return ResponseEntity.ok(wallet);
    }

}
