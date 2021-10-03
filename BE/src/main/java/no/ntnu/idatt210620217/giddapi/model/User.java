package no.ntnu.idatt210620217.giddapi.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
@NaturalIdCache
/**
 * Class for User and database setup
 * @version 1.0
 */
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name="firstname")
    private String firstname;
    @Column(name="lastname")
    private String lastname;
    @Column(name="email", unique=true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;
    @Column(name="password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(name="gender")
    private String gender;
    @Column(name="city")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String city;
    @Column(name="birthyear")
    private short birthyear;

    @JsonIgnore
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<Participant> participants;

    @JsonIgnore
    @OneToMany(mappedBy="organizer", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Activity> organizing;
    
    @OneToMany(
        mappedBy = "equipment", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true)
    @JsonIgnore
    private Set<EquipmentUser> equipmentUsers;

    //Creates many to many relation table with waitlist
    @ManyToMany(mappedBy = "waitlist")
    @JsonIgnore
    private Collection<Activity> myWaitlists = new LinkedList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Rating rating;

    @Column(name = "totalPoints")
    private int totalPoints;

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications;

    /**
     * Empty class constructor
     */
    public User() {
    }
    /**
     * Class constructor for User
     * @param id Id of user
     * @param firstname Firstname of user
     * @param lastname Lastname of user
     * @param email Email of user
     * @param password Password of user
     * @param gender Gender of user
     * @param city City user lives in
     * @param birthyear Birthyear of user
     */
    public User(long id, String firstname, String lastname, String email, String password, String gender, String city, short birthyear) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.city = city;
        this.birthyear = birthyear;
        this.totalPoints = 0;
    }

    /**
     * Getters and setters
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public short getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(short birthyear) {
        this.birthyear = birthyear;
    }

    public Set<EquipmentUser> getEquipmentUsers() {
        return equipmentUsers;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Collection<Activity> getMyWaitlists() {
        return myWaitlists;
    }
    public void setMyWaitlists(Collection<Activity> myWaitlists) {
        this.myWaitlists = myWaitlists;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(this.firstname, user.firstname) && Objects.equals(this.lastname, user.lastname);
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.firstname, this.lastname);
    }

    @JsonIgnore
    public Object getPrivateUser() {
        return new PrivateUser(id, firstname, lastname, email, password, gender, city, birthyear, rating, totalPoints);
    }
}

/**
 * Class for privateUser
 * @version 1.0
 */
class PrivateUser {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String city;
    private short birthyear;
    private Rating rating;
    private int totalPoints;

    /**
     * Getters
     */
    public long getId() {
        return id;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getGender() {
        return gender;
    }
    public String getCity() {
        return city;
    }
    public short getBirthyear() {
        return birthyear;
    }
    public Rating getRating() {
        return rating;
    }
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Constructor for Private user
     * @param id Id of user
     * @param firstname Firstname of user
     * @param lastname Lastname of user
     * @param email Email of user
     * @param password Password of user
     * @param gender Gender of user
     * @param city City user lives in
     * @param birthyear Birthyear of user
     * @param rating Rating of user
     * @param totalPoints Total points of all sports
     */
    public PrivateUser(long id,
                       String firstname,
                       String lastname,
                       String email,
                       String password,
                       String gender,
                       String city,
                       short birthyear,
                       Rating rating,
                       int totalPoints) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.city = city;
        this.birthyear = birthyear;
        this.rating = rating;
        this.totalPoints = totalPoints;
    }
}
