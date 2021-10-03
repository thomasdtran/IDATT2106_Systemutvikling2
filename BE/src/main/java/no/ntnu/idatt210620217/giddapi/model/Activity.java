package no.ntnu.idatt210620217.giddapi.model;

import java.util.*;

import javax.persistence.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "Activity")

/**
 * Class for Activity and database setup
 * @version 1.0
 */
public class Activity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(name = "title")
  private String title;
  @Column(name = "description")
  private String description;
  @Column(name = "location")
  private String location;
  @Column(name= "latitude")
  private double latitude;
  @Column(name= "longitude")
  private double longitude;
  @Column(name = "date")
  private String date;
  @Column(name = "startTime")
  private String startTime;
  @Column(name = "endTime")
  private String endTime;
  @Column(name = "intensity")
  private String intensity;
  @Column(name = "hasSendtEmailRequest")
  private boolean hasSendtEmailRequest;
  @Column(name = "hasReceivedActivityPoints")
  private boolean hasReceivedActivityPoints;

  @ManyToOne
  @JoinColumn(name = "sport")
  private Sport sport;


  @OneToMany(mappedBy="activity", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST)
  private Set<Participant> participants;

  //todo: legge til denne til konstruktøren?
  @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
  @Column(name = "equipment")
  private Collection<Equipment> allEquipments;

  //TODO: add arrangør, aka bruker
  @ManyToOne()
  @JoinColumn(name="user_id")
  private User organizer;

  @Column(name = "maxParticipants")
  private int maxParticipants;

  @ManyToMany()
  @JoinTable(
          name = "waitlist",
          joinColumns = @JoinColumn(name = "activity_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Collection<User> waitlist = new LinkedList<>();


  /**
   * Empty class constructor
   */
  public Activity() {
  }

  /**
   * Constructor
   * Todo: possible to change the date and start/endTime to make it more compact
   * Todo: Can make own object of sport
   * @param id unique
   * @param title of the activity
   * @param description which describes the activity
   * @param location where the activity will take place
   * @param date of which the activity will occur, format åååå-mm-dd
   * @param startTime When the activity starts, format 00:00, military hours
   * @param endTime When the activity ends, format 00:00, military hours
   * @param intensity of the activity
   * @param sport What type the activity is
   * @param maxParticipants Max number of allowed participants
   */
  public Activity(long id, String title, String description, String location, double latitude, double longitude, String date, String startTime,
                  String endTime, String intensity, Sport sport, int maxParticipants) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.location = location;
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = date; //kunn dato
    this.startTime = startTime; //start
    this.endTime = endTime; //slutt
    this.intensity = intensity;
    this.sport = sport;
    this.maxParticipants = maxParticipants;
    this.hasSendtEmailRequest = false;
    this.hasReceivedActivityPoints = false;
  }

  /**
   * Constructor without id but with equipment
   * @param title
   * @param title of the activity
   * @param description which describes the activity
   * @param location where the activity will take place
   * @param date of which the activity will occur, format åååå-mm-dd
   * @param startTime When the activity starts, format 00:00, military hours
   * @param endTime When the activity ends, format 00:00, military hours
   * @param intensity of the activity
   * @param sport What type the activity is
   * @param maxParticipants Max number of allowed participants
   * @param allEqupiments All equipments needed for activity
   */
  public Activity(String title, String description, String location, double latitude, double longitude, String date, String startTime,
    String endTime, String intensity, Sport sport, int maxParticipants, Collection<Equipment> allEqupiments) {
    this.title = title;
    this.description = description;
    this.location = location;
    this.latitude = latitude;
    this.longitude = longitude;
    this.date = date; // kunn dato
    this.startTime = startTime; // start
    this.endTime = endTime; // slutt
    this.intensity = intensity;
    this.sport = sport;
    this.maxParticipants = maxParticipants;
    this.allEquipments = allEqupiments;
    this.hasSendtEmailRequest = false;
    this.hasReceivedActivityPoints = false;

  }



  /**
   * Getters and setters.
   */
  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public User getOrganizer() {
    return organizer;
  }

  public int getMaxParticipants() {
    return maxParticipants;
  }

  public void setOrganizer(User organizer) {
    this.organizer = organizer;
  }

  public void setMaxParticipants(int maxParticipants) {
    this.maxParticipants = maxParticipants;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public double getLatitude() { return latitude; }

  public void setLatitude(double latitude) { this.latitude = latitude; }

  public double getLongitude() { return longitude; }

  public void setLongitude(double longitude) { this.longitude = longitude; }

  public String getDate() {
    return date;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getIntensity() {
    return intensity;
  }

  public boolean isHasReceivedActivityPoints() {
    return hasReceivedActivityPoints;
  }

  public void setHasReceivedActivityPoints(boolean hasReceivedActivityPoints) {
    this.hasReceivedActivityPoints = hasReceivedActivityPoints;
  }

  public boolean isHasSendtEmailRequest() {
    return hasSendtEmailRequest;
  }

  public void setHasSendtEmailRequest(boolean hasSendtEmailRequest) {
    this.hasSendtEmailRequest = hasSendtEmailRequest;
  }

  public Sport getSport() {
    return sport;
  }

  public void setSport(Sport sport) {
    this.sport = sport;
  }


  public void setIntensity(String intensity) {
    this.intensity = intensity;
  }

  public Collection<User> getWaitlist() {
    return this.waitlist;
  }

  public Collection<Equipment> getAllEquipments() {
      return allEquipments;
  }
  public void setAllEquipments(Collection<Equipment> allEquipments) {
      this.allEquipments = allEquipments;
  }
  //todo: create method to add more equipments?

  public Set<Participant> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<Participant> participants) {
    this.participants = participants;
  }
}
