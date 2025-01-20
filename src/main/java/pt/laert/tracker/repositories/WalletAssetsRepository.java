package pt.laert.tracker.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.laert.tracker.model.WalletAssetsEntity;

public interface WalletAssetsRepository extends JpaRepository<WalletAssetsEntity, String> {
    List<WalletAssetsEntity> findAllByWalletId(Long walletId);
}
