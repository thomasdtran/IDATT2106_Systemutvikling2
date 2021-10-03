package no.ntnu.idatt210620217.giddapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Class for participant
 * @version 1.0
 */
@Entity(name = "Participant")
@Table(name = "participant")
public class Participant {
    @JsonIgnore
    @EmbeddedId
    private ParticipantId id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="activity_id", nullable=false, insertable = false, updatable = false)
    private Activity activity;

    @Column(name = "hasRated")
    private boolean hasRated;

    /**
     * Empty constructor
     */
    public Participant() {}

    /**
     * Construcor for participant
     * @param user User to participate in activity
     * @param activity activity for user to participate in
     * @param hasRated boolean if user has rated or not
     */
    public Participant(User user, Activity activity, boolean hasRated) {
        this.id = new ParticipantId(activity.getId(), user.getId());
        this.user = user;
        this.activity = activity;
        this.hasRated = hasRated;
    }

    /**
     * getters and setters
     */
    public ParticipantId getId() {
        return id;
    }

    public void setId(ParticipantId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean getHasRated() {
        return hasRated;
    }

    public void setHasRated(boolean hasRated) {
        this.hasRated = hasRated;
    }
}
