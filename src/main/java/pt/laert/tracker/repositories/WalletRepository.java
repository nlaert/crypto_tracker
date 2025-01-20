package pt.laert.tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.laert.tracker.model.WalletEntity;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
}
