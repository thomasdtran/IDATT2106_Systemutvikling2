package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.SportStats;
import no.ntnu.idatt210620217.giddapi.model.SportUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repo for SportStats
 * @version 1.0
 */
@Repository
public interface SportStatsRepo extends JpaRepository<SportStats, SportUserId> {
    Optional<SportStats> findById(SportUserId sportUserId);
}
