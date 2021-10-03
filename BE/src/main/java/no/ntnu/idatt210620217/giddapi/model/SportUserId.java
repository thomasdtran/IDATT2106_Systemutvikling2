package no.ntnu.idatt210620217.giddapi.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
/**
 * Class for SportUserId
 * @version 1.0
 */
public class SportUserId implements Serializable {
    @Column(name = "sport_id")
    private long sportId;

    @Column(name = "user_id")
    private long userId;

    /**
     * Empty class constructor
     */
    public SportUserId(){}

    /**
     * Class constructor
     * @param sportId Id of sport
     * @param userId Id of user
     */
    public SportUserId(long sportId, long userId) {
        this.sportId = sportId;
        this.userId = userId;
    }

    /**
     * Getters and setters
     */
    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        SportUserId that = (SportUserId) o;
        return Objects.equals(sportId, that.sportId) && Objects.equals(userId, that.userId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(sportId, userId);
    }
}
