package no.ntnu.idatt210620217.giddapi.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class for ParticipantId
 * @version 1.0
 */
@Embeddable
public class ParticipantId implements Serializable {
    @Column(name = "activity_id")
    private long activityId;

    @Column(name = "user_id")
    private long userId;

    /**
     * Empty constructor
     */
    public ParticipantId(){}

    /**
     * Constructor for participantId
     * @param activityId Id of activity
     * @param userId Id of user
     */
    public ParticipantId(long activityId, long userId) {
        this.activityId = activityId;
        this.userId = userId;
    }

    /**
     * getters and setters
     */
    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return activityId == that.activityId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, userId);
    }
}
