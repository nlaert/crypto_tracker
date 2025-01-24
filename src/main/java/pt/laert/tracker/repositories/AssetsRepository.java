package pt.laert.tracker.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.laert.tracker.model.AssetEntity;

public interface AssetsRepository extends JpaRepository<AssetEntity, String> {
    Optional<AssetEntity> findBySymbol(String symbol);

    List<AssetEntity> findAllBySymbolIn(List<String> symbols);
}
