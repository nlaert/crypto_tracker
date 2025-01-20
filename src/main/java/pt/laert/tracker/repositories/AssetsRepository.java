package pt.laert.tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.laert.tracker.model.AssetEntity;

public interface AssetsRepository extends JpaRepository<AssetEntity, String> {
}
