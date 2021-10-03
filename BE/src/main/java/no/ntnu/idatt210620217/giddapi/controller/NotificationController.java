package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.model.Notification;
import no.ntnu.idatt210620217.giddapi.Service.NotificationService;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@CrossOrigin
public class NotificationController {
    
    @Autowired
    private NotificationService service;

    Logger logger = LoggerFactory.getLogger(NotificationController.class);

    /**
     * Method to retrieve all the notifications connected to a user
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllNotificationsForUser(@PathVariable long userId){
        Set<Notification> notifications = service.getAllNotificationsForUser(userId);
        if(!(notifications.isEmpty()) || notifications != null){
            logger.info("Retrieved all notifications for user: " + userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(notifications,HttpStatus.BAD_REQUEST);
        }  
    }

    /**
     * Creates a notification for a specific user
     * @param notification Contains the user
     * @return
     */
    @PostMapping("/{userId}")
    public ResponseEntity<?> createNotification(@RequestBody Notification notification, @PathVariable long userId){
        Notification createdNotification = service.createNotification(notification, userId);
        if (createdNotification != null) {
            logger.info("Sucessfully created notification");
            return new ResponseEntity<>(createdNotification, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method to update a notification when its read
     * @param notificationId
     * @return
     */
    @PutMapping("/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable long notificationId){
        Notification notification = service.markAsRead(notificationId);
        if (notification != null) {
            logger.info("Sucessfully marked as read for notification with id: " +  notificationId);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
