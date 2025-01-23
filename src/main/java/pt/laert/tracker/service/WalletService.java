package pt.laert.tracker.service;

import org.springframework.stereotype.Service;
import pt.laert.tracker.error.WalletAlreadyExistsException;
import pt.laert.tracker.error.WalletNotFoundException;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.model.WalletAssetsEntity;
import pt.laert.tracker.model.WalletEntity;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.repositories.WalletAssetsRepository;
import pt.laert.tracker.repositories.WalletRepository;

@Service
public class WalletService {
    private final CoinCapService coinCapService;
    private final WalletRepository walletRepository;
    private final AssetsRepository assetsRepository;
    private final WalletAssetsRepository walletAssetsRepository;

    public WalletService(CoinCapService coinCapService,
                         WalletRepository walletRepository,
                         AssetsRepository assetsRepository,
                         WalletAssetsRepository walletAssetsRepository) {
        this.coinCapService = coinCapService;
        this.walletRepository = walletRepository;
        this.assetsRepository = assetsRepository;
        this.walletAssetsRepository = walletAssetsRepository;
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

    public void addAssetToWallet(Long id, Asset asset) {
        var wallet = walletRepository.findById(id);
        if (wallet.isEmpty()) {
            throw new WalletNotFoundException(id);
        }
        // If asset already exists in our DB, we don't need to call CoinCap
        var persistedAsset = assetsRepository.findBySymbol(asset.getSymbol());
        if (persistedAsset.isEmpty()) {
            createAsset(asset);
        }
        var walletAsset = new WalletAssetsEntity(
                id,
                asset.getSymbol(),
                asset.getQuantity(),
                asset.getPrice()
        );
        walletAssetsRepository.save(walletAsset);
    }

    private void createAsset(Asset asset) {
        var coinData = coinCapService.searchForAsset(asset.getSymbol());
        if (coinData == null) {
            throw new IllegalArgumentException("Asset does not exist: " + asset.getSymbol());
        }
        var assetEntity = new AssetEntity(
                coinData.getSymbol(),
                coinData.getName(),
                coinData.getPriceAsDouble()
        );
        assetsRepository.save(assetEntity);
    }
}
