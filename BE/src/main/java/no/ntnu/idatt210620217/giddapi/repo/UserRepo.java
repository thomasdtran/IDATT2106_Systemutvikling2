package no.ntnu.idatt210620217.giddapi.repo;

import no.ntnu.idatt210620217.giddapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo for User
 * @version 1.0
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findById(long id);
    User findUserByEmail(String email);
}
