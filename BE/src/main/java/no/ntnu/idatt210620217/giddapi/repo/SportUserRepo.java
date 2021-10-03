package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.SportStats;
import no.ntnu.idatt210620217.giddapi.model.SportUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo for SportUser
 * @version 1.0
 */
@Repository
public interface SportUserRepo extends JpaRepository<SportStats, SportUserId> {
}
