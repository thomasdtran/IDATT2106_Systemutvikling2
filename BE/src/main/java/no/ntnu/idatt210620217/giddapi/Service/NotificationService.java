package no.ntnu.idatt210620217.giddapi.Service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.ntnu.idatt210620217.giddapi.model.Notification;
import no.ntnu.idatt210620217.giddapi.model.Participant;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.NotificationRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;

/**
 * Service class for notifications
 * @version 1.0
 */
@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Method for retrieving all notifications from user
     * @param userId Id of user to get all notifications from
     * @return List with all notifications connected to user
     */
    public Set<Notification> getAllNotificationsForUser(long userId){
        //Checks if the user exists 
        User user = userRepo.findById(userId);
        if(user != null){
            Set<Notification> notifications = notificationRepo.findByUserId(userId);
            if(notifications != null){
                return notifications;
            }else{
                logger.warn("No notifications found for user with id: " + userId);
                return null;
            }
        }else{
            logger.error("User not found with id: " + userId);
            return null;
        }
        
    }

    /**
     * Method for creating new notification
     * @param notification Notification object to be created
     * @param userId Id of user to recieve notification
     * @return Notification
     */
    public Notification createNotification(Notification notification, long userId){
        // Checks if the user exists
        User user = userRepo.findById(userId);
        if (user != null) {
            notification.setUser(user);
            return notificationRepo.save(notification);
        } else {
            logger.error("User not found with id: " + userId);
            return null;
        }
        
    }

    /**
     * Method to send notification to more people at the same time
     * @param notification Notification object to be created
     * @param participants Participants of activity to recieve notification
     */
    public void createNotification(Notification notification, Set<Participant> participants) {
        if(!(participants.isEmpty()) && (participants != null)){
            for (Participant participant : participants) {
                User user = participant.getUser();
                if (user != null) {
                    notification.setUser(user);
                    notificationRepo.save(notification);
                } else {
                    logger.error("User not found");
                }
            }
        }else{
            logger.error("Participant list is empty or null");
        }
    }

    /**
     * Method for marking notification as read
     * @param notificationId Id of notification to be marked as read
     * @return Notification
     */
    public Notification markAsRead(long notificationId){
        Notification notification = notificationRepo.findById(notificationId);

        if(notification != null){
            notification.markAsRead();
            return notificationRepo.save(notification);
        }else{
            logger.error("Notification not found with id: " + notificationId);
            return null;
        }        
    }
}
