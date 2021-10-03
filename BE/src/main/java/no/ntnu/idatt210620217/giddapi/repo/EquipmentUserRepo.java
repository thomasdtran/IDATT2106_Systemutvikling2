package no.ntnu.idatt210620217.giddapi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ntnu.idatt210620217.giddapi.model.EquipmentUser;
import no.ntnu.idatt210620217.giddapi.model.EquipmentUserId;

/**
 * Repo EquipmentUser
 * @version 1.0
 */
@Repository
public interface EquipmentUserRepo extends JpaRepository<EquipmentUser, EquipmentUserId>{
}
