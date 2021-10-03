package no.ntnu.idatt210620217.giddapi.model;

/**
 * Class for SportStatsInfo
 * @version 1.0
 */
public class SportStatsInfo {
    private long sportId;
    private long userId;
    private int points;

    /**
     * Class constructor
     * @param sportId Id of sport that points is of
     * @param userId Id of user that has points in sport
     * @param points Number of points user has in sport
     */
    public SportStatsInfo(long sportId, long userId, int points) {
        this.sportId = sportId;
        this.userId = userId;
        this.points = points;
    }

    /**
     * Getters
     */
    public long getSportId() {
        return sportId;
    }

    public long getUserId() {
        return userId;
    }

    public int getPoints() {
        return points;
    }
}
