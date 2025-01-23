package pt.laert.tracker.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.laert.tracker.error.WalletAlreadyExistsException;
import pt.laert.tracker.error.WalletNotFoundException;
import pt.laert.tracker.model.AssetEntity;
import pt.laert.tracker.model.WalletAssetsEntity;
import pt.laert.tracker.model.WalletEntity;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.repositories.AssetsRepository;
import pt.laert.tracker.repositories.WalletAssetsRepository;
import pt.laert.tracker.repositories.WalletRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private CoinCapService coinCapService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AssetsRepository assetsRepository;

    @Mock
    private WalletAssetsRepository walletAssetsRepository;

    @Test
    void testCreateWallet_NullEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet(null);
        });
        assertEquals("User email cannot be null", exception.getMessage());
    }

    @Test
    void testCreateWallet_BlankEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet("   ");
        });
        assertEquals("User email cannot be null", exception.getMessage());
    }

    @Test
    void testCreateWallet_ExistingWallet() {
        String userEmail = "test@example.com";
        when(walletRepository.findByEmail(userEmail)).thenReturn(Optional.of(new WalletEntity(userEmail)));

        assertThrows(WalletAlreadyExistsException.class, () -> walletService.createWallet(userEmail));
    }

    @Test
    void testCreateWallet_Success() {
        String userEmail = "test@example.com";
        when(walletRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        WalletEntity walletEntity = new WalletEntity(userEmail);
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        Wallet wallet = walletService.createWallet(userEmail);

        assertNotNull(wallet);
        assertEquals(walletEntity.getEmail(), wallet.getEmail());
        assertEquals(walletEntity.getId(), wallet.getWalletId());
    }

    @Test
    void testAddAssetToWallet_WalletNotFound() {
        Long walletId = 1L;
        Asset asset = new Asset("BTC", BigDecimal.valueOf(1.0), 50000.0);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.addAssetToWallet(walletId, asset));
    }

    @Test
    void testAddAssetToWallet_ExistingAsset() {
        Long walletId = 1L;
        WalletEntity walletEntity = new WalletEntity("test@example.com");
        walletEntity.setId(walletId);
        Asset asset = new Asset("BTC", BigDecimal.valueOf(1.0), 50000.0);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(assetsRepository.findBySymbol(asset.getSymbol())).thenReturn(Optional.of(new AssetEntity(asset.getSymbol(), "Bitcoin", 50000.0)));

        walletService.addAssetToWallet(walletId, asset);

        verify(assetsRepository, never()).save(any());
        verify(walletAssetsRepository).save(any(WalletAssetsEntity.class));
    }

    @Test
    void testAddAssetToWallet_NonExistingAsset() {
        Long walletId = 1L;
        WalletEntity walletEntity = new WalletEntity("test@example.com");
        walletEntity.setId(walletId);
        Asset asset = new Asset("DOT", BigDecimal.valueOf(1.0), 500.0);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(assetsRepository.findBySymbol(asset.getSymbol())).thenReturn(Optional.empty());
        when(coinCapService.searchForAsset(asset.getSymbol())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.addAssetToWallet(walletId, asset);
        });
        assertEquals("Asset does not exist: " + asset.getSymbol(), exception.getMessage());
    }

    @Test
    void testAddAssetToWallet_Success() {
        Long walletId = 1L;
        WalletEntity walletEntity = new WalletEntity("test@example.com");
        walletEntity.setId(walletId);
        Asset asset = new Asset("LTC", BigDecimal.valueOf(1.0), 200.0);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        when(assetsRepository.findBySymbol(asset.getSymbol())).thenReturn(Optional.empty());
        CoinData coinData = new CoinData("litecoin", asset.getSymbol(), "Litecoin", BigDecimal.valueOf(200.0)); // Assuming CoinData is a class that holds asset info
        when(coinCapService.searchForAsset(asset.getSymbol())).thenReturn(coinData);

        walletService.addAssetToWallet(walletId, asset);

        verify(assetsRepository).save(any(AssetEntity.class));
        verify(walletAssetsRepository).save(any(WalletAssetsEntity.class));
    }
}
