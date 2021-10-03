package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo for Sport
 * @version 1.0
 */
@Repository
public interface SportRepo extends JpaRepository<Sport, Long> {
    Sport findById(long id);
}
