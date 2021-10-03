package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.Participant;
import no.ntnu.idatt210620217.giddapi.model.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repo for ParticipantRepo
 * @version 1.0
 */
public interface ParticipantRepo extends JpaRepository<Participant, ParticipantId> {
    Participant findByActivityIdAndUserId(long activityId, long userId);
}
