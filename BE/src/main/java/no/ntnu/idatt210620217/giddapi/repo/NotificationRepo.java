package no.ntnu.idatt210620217.giddapi.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ntnu.idatt210620217.giddapi.model.Notification;

/**
 * Repo for Notification
 * @version 1.0
 */
@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long>{
    Set<Notification> findByUserId(long id);
    Notification findById(long id);
}
