package no.ntnu.idatt210620217.giddapi.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idatt210620217.giddapi.controller.UserController;
import no.ntnu.idatt210620217.giddapi.emailUtil.EmailService;
import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.model.Notification;
import no.ntnu.idatt210620217.giddapi.model.Participant;
import no.ntnu.idatt210620217.giddapi.model.Rating;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.notificationUtil.NotificationGenerator;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.ParticipantRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.AccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Service class for Activity
 * @version 1.0
 */
@Configuration
@EnableScheduling
@EnableAsync
@Service
public class ActivityService {

  @Autowired
  private ActivityRepo activityRepo;
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private ParticipantRepo participantRepo;
  @Autowired
  private EmailService emailService;
  @Autowired
  private NotificationService notificationService;

  Logger logger = LoggerFactory.getLogger(ActivityService.class);

  /**
   * Method for finding all activities. Calls method in Repo class.
   * @return List with all activities.
   */
  public List<Activity> findAllActivities() {
    return activityRepo.findAll();
  }

  /**
   * Method for finding activity by Id. Calls method in Repo class.
   * @param id of the specific object
   * @return The activity with this id or null if user does not exist
   */
  public Activity findActivityById(long id) {
    return activityRepo.findById(id);
  }

  /**
   * Method for finding activities by specific title. Calls method in Repo class.
   * @param title a specific title
   * @return List with all activities with this title
   */
  public List<Activity> findActivityByTitle(String title) {
    List<Activity> allActivities = activityRepo.findAll();
    List<Activity> foundActivities = new ArrayList<>();

    for(int i = 0; i < allActivities.size(); i ++){
      String s = allActivities.get(i).getTitle();
      if(s.contains(title)) {
        foundActivities.add(allActivities.get(i));
      }
    }
    return foundActivities;
  }

  /**
   * Method for creating new activity. Checks if the important information is provided. Calls method in Repo class.
   * @param activity to be created
   * @param organizerId of organizer of this activity
   * @return created activity
   */
  public Activity createActivity(Activity activity, long organizerId) throws AccessException {
    if(!isValidOrganizer(organizerId)){
      return null; //organizer does not exist.
    }
    if (!isValidDate(activity.getDate())){
      return null; //date it wrong
    } else if(!isValidTime(activity.getEndTime()) || !isValidTime(activity.getStartTime())){
      return null; //time is wrong
    } else if(!hasInfo(activity.getTitle()) || !hasInfo(activity.getLocation()) || !hasInfo(activity.getSport()) || activity.getMaxParticipants()<0){
      return null;//info is wrong
    } else if(!hasInfo(activity.getDescription())){
      activity.setDescription(" "); //description empty, add empty string ass descript
    }
    User organizer = userRepo.findById(organizerId);
    activity.setOrganizer(organizer);

    BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(authenticatedUser.getId() != activity.getOrganizer().getId())
      throw new AccessException("Cannot update activity when authenticated user is not organizer");

    activity = activityRepo.save(activity);
    Participant participant = new Participant(organizer, activity, false);
    participantRepo.save(participant);

    return activity;
  }

  /**
   * Method for updating an activity. Every if statement checks
   * if the new information is different and not null before updating.
   * Sends notification to the users for important changes
   * @param newActivityData object with all the new information
   * @param id of the activity to be updated
   * @return updated activity or null if not valid info
   */
  public Activity updateActivity(Activity newActivityData, long id) throws AccessException {
    Activity current = activityRepo.findById(id);

    //Prepares the title for the notification
    String title = "En aktivitet har blitt oppdatert";
    String path = "#";
    
    //checks if authenticated user is also organizer of activity
    BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(current != null && authenticatedUser.getId() != current.getOrganizer().getId())
      throw new AccessException("Cannot update activity when authenticated user is not organizer");

    if((!(current.getTitle().equals(newActivityData.getTitle()))) && (newActivityData.getTitle()!=null)){
      current.setTitle(newActivityData.getTitle());
    }
    if((!(current.getDescription().equals(newActivityData.getDescription()))) && (newActivityData.getDescription()!=null)){
      current.setDescription(newActivityData.getDescription());
    }
    if((!(current.getLocation().equals(newActivityData.getLocation()))) && (newActivityData.getLocation()!=null)){
      // Sends notfication that the activity has changed its location
      String desc = "'" + current.getTitle() + "'" + " har byttet lokasjon til " + newActivityData.getLocation();
      sendNotification(current, title, desc, path);

      current.setLocation(newActivityData.getLocation());
    }
    if((!(current.getLatitude()==(newActivityData.getLatitude()))) && (newActivityData.getLatitude()!=0)){
      current.setLatitude(newActivityData.getLatitude());
    }
    if((!(current.getLongitude()==(newActivityData.getLongitude()))) && (newActivityData.getLongitude()!=0)){
      current.setLongitude(newActivityData.getLongitude());
    }
    if((!(current.getDate().equals(newActivityData.getDate()))) && (newActivityData.getDate()!=null)){
      if( isValidDate(newActivityData.getDate())){
        current.setDate(newActivityData.getDate());
      } else {
        return null; //invalid date
      }
    }
    if((!(current.getStartTime().equals(newActivityData.getStartTime()))) && (newActivityData.getStartTime()!=null)){
      if( isValidTime(newActivityData.getStartTime())){
        //Sends notification that the starttime has changed
        String desc = "'" + current.getTitle() + "'" + " har byttet start tid til " + newActivityData.getStartTime();
        sendNotification(current, title, desc, path);

        current.setStartTime(newActivityData.getStartTime());
      } else {
        return null; //invalid startTime
      }
    }
    if((!(current.getEndTime().equals(newActivityData.getEndTime()))) && (newActivityData.getEndTime()!=null)){
      if( isValidTime(newActivityData.getEndTime())){
        // Sends notification that the endtime has changed
        String desc = "'" + current.getTitle() + "'" + " har byttet slutt tid til " + newActivityData.getEndTime();
        sendNotification(current, title, desc, path);

        current.setEndTime(newActivityData.getEndTime());
      } else {
        return null; //invalid endtime
      }
    }
    if((!(current.getSport().equals(newActivityData.getSport()))) && (newActivityData.getSport()!=null)){
      current.setSport(newActivityData.getSport());
    }
    if((!(current.getIntensity().equals(newActivityData.getIntensity()))) && (newActivityData.getIntensity()!=null)){
      current.setIntensity(newActivityData.getIntensity());
    }
    if((!(current.getOrganizer().equals(newActivityData.getOrganizer()))) && (newActivityData.getOrganizer()!=null)){
      if( isValidOrganizer(newActivityData.getOrganizer().getId())){
        current.setOrganizer(newActivityData.getOrganizer());
      } else {
        return null; //invalid organizer
      }
    }
    if((current.getMaxParticipants()!=newActivityData.getMaxParticipants()) && (newActivityData.getMaxParticipants()!=0)){
      current.setMaxParticipants(newActivityData.getMaxParticipants());
    }
    return activityRepo.save(current);
  }

  /**
   * Method for deleting an activity. This does not check if any users are connected to this activity.
   * todo: check if it is safe to delete.
   * @param id of the activity to be deleted
   */
  public void deleteActivity(long id) throws AccessException {
    Activity activity = activityRepo.findById(id);

    //checks if authenticated user is also organizer of activity
    BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(activity != null && authenticatedUser.getId() != activity.getOrganizer().getId())
      throw new AccessException("Cannot delete activity when authenticated user is not organizer");

    //Sends notfication that the activity is cancelled
    String title = "Aktivitet kansellert";
    String desc = "Aktiviteten du meldte deg på med tittel '" + activity.getTitle() + "' er kanselert";
    String path = "#";
    sendNotification(activity, title, desc, path);
        
    activityRepo.deleteById(id);
  }

  /**
   * Method for giving rating to organizer of activity
   * @param activityId id of activity that organizer has organized
   * @param userId id of user that is rating organizer
   * @param rating rating to be given to organizer
   */
  public void createRating(long activityId, long userId, int rating) {
    Activity activity = activityRepo.findById(activityId);
    User user = userRepo.findById(userId);
    Participant participant = getParticipantByUserId(activity.getParticipants(), userId);

    if(activity == null || user == null || participant == null)
      throw new IllegalArgumentException("User, activity or participant was null");

    if(participant.getHasRated())
      throw new IllegalArgumentException("User has already rated activity");

    if(rating > 5 || rating < 1)
      throw new IllegalArgumentException("Invalid rating");

    User organizer = activity.getOrganizer();
    if(organizer.getRating() == null) organizer.setRating(new Rating(organizer));
    organizer.getRating().addRating(rating);
    participant.setHasRated(true);
    activityRepo.save(activity);
  }

  /**
   * Method for checking if the time is sumbitted in the format 00:00
   * @param time to be checked
   * @return boolean confirming if the time is formatted correct
   */
  public boolean isValidTime(String time){
    String[] list = time.split(":");
    if(list[0].length()!=2 || list[1].length()!=2 ||list.length>2){
      return false;
    } else if(Integer.parseInt(list[0])>23 || Integer.parseInt(list[0])<0){
      return false;
    } else if(Integer.parseInt(list[1])>59 || Integer.parseInt(list[1])<0){
      return false;
    }
    return true;
  }

  /**
   * Method for checking if the date is sumbitted in the format yyyy-mm-dd
   * todo: sjekk dagens dato. Slik man ikke kan opprette arrangement på en dato som allerede har vært
   * @param date to be checked
   * @return boolean confirming if the date is formatted correct
   */
  public boolean isValidDate(String date){
    String[] list = date.split("-");
    if(list[0].length()!=4 || list[1].length()!=2 || list[2].length()!=2 || list.length>3){
      return false;
    } else if (Integer.parseInt(list[0])< Year.now().getValue()){
      return false;
    } else if (Integer.parseInt(list[1])>12 || Integer.parseInt(list[1])<0){
      return false;
    }else if (Integer.parseInt(list[2])>31 || Integer.parseInt(list[2])<0){
      return false;
    }
    return true;
  }

  /**
   * Method for checking if the object has info stored
   * todo: meant for title, location, intensity and sport. These object may change and will need different checks.
   * @param object object to be checked
   * @return boolean confirming if the object has info
   */
  public boolean hasInfo(String object){
    return object != null;
  }

  /**
   * Method for checking if the object has info stored
   * @param object object to be checked
   * @return boolean confirming if the object has info
   */
  public boolean hasInfo(Object object){
    return object != null;
  }

  /**
   * Method for checking if the organizer is valid.
   * todo: should perhaps check if they are logged in?
   * @param organizerId object to be checked
   * @return boolean confirming if the organizer is valid
   */
  public boolean isValidOrganizer(long organizerId){
    return userRepo.findById(organizerId) != null;
  }

  /**
   * Method for retrieving participant by user id
   * @param participants List of all participants in an activity
   * @param userId id of the user to find in list of participants
   * @return participant with user id = userId
   */
  private Participant getParticipantByUserId(Set<Participant> participants, long userId) {
    for(Participant participant : participants) {
      if(participant.getUser().getId() == userId) return participant;
    }
    return null;
  }

  /**
   * Scheduler method, watcher if an activity is over.
   * Sends mail to participants when activity is over
   */
  @Async
  @Scheduled(fixedRate = 60000) // In reality, should run every 15 min 900000ms
  public void activityIsOverScheduler() {
    for (Activity activity : activityRepo.findAll()){
      if (!activity.isHasSendtEmailRequest()){
        // Dates for "now-time" and activity
        LocalDate nowDate = LocalDate.now();
        LocalDate activityDate = LocalDate.parse(activity.getDate());
        // checks if activity day is before this months day
        if (!activityDate.isBefore(nowDate)){
          // checks if activity day is equal to today, and if activity (in hours) is less than "now-time"
          if (nowDate.equals(activityDate) && LocalTime.parse(activity.getEndTime()).isBefore(LocalTime.now())){
            sendMailToParticipants(activity);

            //Prepares and sends the notification
            String title = "Rate aktivitet!";
            String desc = "'" + activity.getTitle() + "'" + " er over, gjerne gi en rating!";
            String path = "activity/" + activity.getId();
            sendNotification(activity, title, desc, path);
          }
        } else {
          // If activity-day of month is less than "now-time"-day of month
          sendMailToParticipants(activity);
        }
      }
    }
  }


  /**
   * Method for sending mail to participants of activity
   * @param activity Activity that participants has attended to recieve email
   */
  private void sendMailToParticipants(Activity activity){
    for (Participant participant : activity.getParticipants()){
      sendMailForRating(participant.getUser().getEmail(), activity.getTitle(), activity.getId());
    }
    activity.setHasSendtEmailRequest(true);
    activityRepo.save(activity);
  }

  /**
   * Method to send notifications to users connected to specific acitvities when
   * the acitvities changes
   * @param activity Activity that changed
   * @param title Title of the notification
   * @param desc Description of notification
   */
  private void sendNotification(Activity activity, String title, String desc, String path){
    Set<Participant> participants = activity.getParticipants();
    for (Participant participant : participants) {
      if(participant.getUser().getId() == activity.getOrganizer().getId()){
        participants.remove(participant);
      }
    }
    NotificationGenerator generator = new NotificationGenerator();
    Notification notification = generator.generate(title, desc, path);
    notificationService.createNotification(notification, participants);
  }

  /**
   * Method for sending email including rating link
   * @param email email of user to send email to
   * @param activityName Name of activity
   * @param activity_id Id of activity
   */
  private void sendMailForRating(String email, String activityName, long activity_id){
    User user = userRepo.findUserByEmail(email);
    if (user == null) throw new NullPointerException("User with this email was not found");
    emailService.sendEmail(email, "Gidd! varsler", "Du var nettopp med på arrangementet "+ activityName + ". Vennligst gi en tilbakemelding til arrangøren ved å følge linken her! \n" +
            "http://localhost:8080/activity/" + activity_id);
  }




}
