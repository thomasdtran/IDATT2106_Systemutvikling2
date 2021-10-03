package no.ntnu.idatt210620217.giddapi.model;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="notification")
/**
 * Class for Activity and database setup
 * @version 1.0
 */
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "sent_date")
    private String date;

    @Column(name = "sent_time")
    private String time;
    
    @Column(name = "is_read")
    private boolean read;

    @Column(name ="path")
    private String path;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Empty constructor
     */
    public Notification(){}

    /**
     * Constructor
     * @param title Title of notification
     * @param description Desciption of notification
     * @param date Date of when notification is issued
     * @param time Time of when notification is issued
     * @param user User to issue notification to
     */
    public Notification(String title, String description, String date, String time, User user){
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.read = false;
        this.user = user;

    }

    /**
     * Constructor for notification with path instead of user
     * @param title Title of notification
     * @param description Desciption of notification
     * @param date Date of when notification is issued
     * @param time Time of when notification is issued
     * @param path Link for user to press in notification
     */
    public Notification(String title, String description, String date, String time, String path) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.read = false;
        this.path = path;
    }

    /**
     * Getters and setters
     */
    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    
    public boolean getRead(){
        return this.read;
    }

    public User getUser() {
        return user;
    }
    public String getPath() {
        return path;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void markAsRead(){
        this.read = true;
    }
}
