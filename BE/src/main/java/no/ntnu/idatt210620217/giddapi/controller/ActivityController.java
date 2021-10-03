package no.ntnu.idatt210620217.giddapi.controller;

import java.util.List;
import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.Service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for Activities
 */
@RestController
@RequestMapping("/api/v1/activities")
@CrossOrigin
public class ActivityController {

  @Autowired
  ActivityService activityService;
  
  Logger logger = LoggerFactory.getLogger(ActivityController.class);


  /**
   * GetMapping for finding all activities
   * @return a list with all the activities
   */
  @GetMapping
  public ResponseEntity<?> findAllActivities() {
    List<Activity> allActivities = activityService.findAllActivities();
    logger.info("All activities shown");
    return new ResponseEntity<>(allActivities, HttpStatus.OK);
  }

  /**
   * GetMapping for finding activity by id
   * @param id PathVariable from client
   * @return activity and HttpStatus.OK or HttpStatus.BAD_REQUEST
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> findActivityById(@PathVariable long id) {
    Activity activity = activityService.findActivityById(id);
    if(activity == null) {
      logger.info("Activity was not found");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    logger.info("Found activity with id: " + id);
    return new ResponseEntity<>(activity, HttpStatus.OK);
  }

  /**
   * GetMapping for finding activity by title
   * @param title PathVariable from client
   * @return list with activities and HttpStatus.OK or HttpStatus.BAD_REQUEST
   */
  @GetMapping("/trigger/{title}")
  public ResponseEntity<?> findActivityByTitle(@PathVariable String title) {
    List<Activity> activity = activityService.findActivityByTitle(title);
    if (activity==null){
      logger.info("Activity with title: " + title + " not found");
    }
    logger.info("Client searched for activity with title: " + title);
    return new ResponseEntity<>(activity, HttpStatus.OK);
  }

  /**
   * PostMapping for creating an activity
   * @param activity RequestBody from client
   * @return activity and HttpStatus.CREATED or HttpStatus.BAD_REQUEST
   */
  @PostMapping("/{organizerId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> createActivity(@RequestBody Activity activity, @PathVariable long organizerId) {
    try{
      Activity activity1 = activityService.createActivity(activity, organizerId);
      if(activity1==null){
        logger.info("Wrong format was given in creating of the activity :))");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      logger.info("New activity was created");
      return new ResponseEntity<>(activity1, HttpStatus.CREATED);
    }catch(AccessException accessException) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }catch (Exception e) {
      logger.info("Wrong format was given in creating of the activity. An exception was thrown.");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * PutMapping for updating acivity info
   * @param activity Requesting Body with new activity info
   * @param id of activity, PathVariable from client
   * @return Activity and HttpStatus.OK if all ok or BAD_REQUEST
   */
  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateActivity(@RequestBody Activity activity, @PathVariable long id) {
    Activity updatedActivity = null;
    try {
      updatedActivity = activityService.updateActivity(activity, id);
    } catch (AccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }

    if (updatedActivity == null){
      logger.info("Wrong format was given when updating activity");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    logger.info("Updating activity with id:" + id);
    return new ResponseEntity<>(updatedActivity, HttpStatus.OK);
  }


  //todo: Make sure nothing needs to be deleted before activity is deleted.
  /**
   * DeleteMapping for deleting an activity
   * @param id PathVariable which is the users id
   * @return HttpStatus.OK
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> deleteActivity(@PathVariable long id) {
    try {
      activityService.deleteActivity(id);
      logger.info("Deleted activity");
    } catch (AccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Method for creating a rating to give to organizer of activity
   * @param activityId activity that organizer has organized
   * @param userId user giving rating
   * @param rating the rating to be given
   * @return status code if successfull or not
   */
  @PostMapping("/{activityId}/rating/{userId}/{rating}")
  public ResponseEntity<?> createRating(@PathVariable long activityId,
                                        @PathVariable long userId,
                                        @PathVariable int rating) {

    try {
      activityService.createRating(activityId, userId, rating);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      logger.warn(e.getMessage());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
