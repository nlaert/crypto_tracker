package pt.laert.tracker.service;

import org.springframework.stereotype.Service;
import pt.laert.tracker.error.WalletAlreadyExistsException;
import pt.laert.tracker.model.WalletEntity;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.repositories.WalletRepository;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final AssetsRepository assetsRepository;

    public WalletService(WalletRepository walletRepository, AssetsRepository assetsRepository) {
        this.walletRepository = walletRepository;
        this.assetsRepository = assetsRepository;
    }

    public Wallet createWallet(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email cannot be null");
        }
        walletRepository.findByEmail(userEmail).ifPresent(wallet -> {
            throw new WalletAlreadyExistsException();
        });
        WalletEntity wallet = new WalletEntity(userEmail);
        wallet = walletRepository.save(wallet);
        return new Wallet(
                wallet.getId(),
                wallet.getEmail()
        );
    }
}
