package no.ntnu.idatt210620217.giddapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Class for rating
 * @version 1.0
 */
@Entity(name = "Rating")
@Table(name = "rating")
public class Rating {
    @Id
    @Column(name = "user_id")
    private long id;
    @Column(name="ratingScore")
    double ratingScore;
    @Column(name="nrOfRatings")
    int nrOfRatings;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Constructor for rating
     * @param ratingScore total score of ratings
     * @param nrOfRatings number of ratings recieved
     */
    public Rating(int ratingScore, int nrOfRatings){
        this.ratingScore = ratingScore;
        this.nrOfRatings = nrOfRatings;
    }

    /**
     * Empty constructor
     */
    public Rating() {
    }

    /**
     * Constructor with user
     * @param user User that has rating
     */
    public Rating(User user) {
        this.user = user;
    }

    /**
     * Getters and setters
     */
    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(int ratingScore) {
        this.ratingScore = ratingScore;
    }

    public int getNrOfRatings() {
        return nrOfRatings;
    }

    public void setNrOfRatings(int nrOfRatings) {
        this.nrOfRatings = nrOfRatings;
    }

    /**
     * Method for adding rating to total ratingscore
     * @param rating Rating to be added to total ratingscore
     */
    public void addRating(int rating) {
        double numerator = ratingScore*nrOfRatings+rating;
        nrOfRatings = nrOfRatings + 1;
        ratingScore = numerator/nrOfRatings;
    }
}
