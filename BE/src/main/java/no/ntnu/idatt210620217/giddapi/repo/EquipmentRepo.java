package no.ntnu.idatt210620217.giddapi.repo;

import org.springframework.stereotype.Repository;
import no.ntnu.idatt210620217.giddapi.model.Equipment;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for EquipmentRepo
 * @version 1.0
 */
@Repository
public interface EquipmentRepo extends JpaRepository<Equipment, Long>{
    Equipment findById(long id);
    Equipment deleteById(long id);
    Collection<Equipment> findByActivityId(long id);
}
