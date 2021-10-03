package no.ntnu.idatt210620217.giddapi.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sportStats")
/**
 * Class for SportStats
 * @version 1.0
 */
public class SportStats {
    @EmbeddedId
    private SportUserId id;

    @ManyToOne
    @MapsId("sportId")
    private Sport sport;

    @ManyToOne
    @MapsId("userId")
    User user;

    @Column(name = "points")
    private int points;

    /**
     * Empty class constructor
     */
    public SportStats(){}

    /**
     * Class constructor
     * @param sport Sport object that points is of
     * @param user User that owns the points
     * @param points Number of points user has in sport
     */
    public SportStats(Sport sport, User user, int points){
        this.sport = sport;
        this.user = user;
        this.points = points;
        this.id = new SportUserId(sport.getId(), user.getId());
    }

    /**
     * Getters and setters
     */
    public SportUserId getId() {
        return id;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        SportStats that = (SportStats) o;
        return Objects.equals(sport, that.sport) && Objects.equals(user, that.user);
    }
    @Override
    public int hashCode() {
        return Objects.hash(sport, user);
    }
}
