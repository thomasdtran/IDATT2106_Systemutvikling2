package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Activity.
 * @version 1.0
 */
@Repository
public interface ActivityRepo extends JpaRepository<Activity, Long> {
  Activity findById(long id);
}
