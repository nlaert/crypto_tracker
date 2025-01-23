package pt.laert.tracker.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.laert.tracker.model.WalletEntity;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByEmail(String email);
}
