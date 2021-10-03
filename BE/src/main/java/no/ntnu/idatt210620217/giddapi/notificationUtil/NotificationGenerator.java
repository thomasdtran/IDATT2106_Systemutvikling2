package no.ntnu.idatt210620217.giddapi.notificationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import no.ntnu.idatt210620217.giddapi.model.Notification;

/**
 * Class to generate a notification with the current date and time
 * @version 1.0
 */
public class NotificationGenerator {

    /**
     * Method for generating Notification
     * @param title Title of notification
     * @param desc Description of notification
     * @param path Path Link to add in notification
     * @return Created notification
     */
    public Notification generate(String title, String desc, String path){

        LocalDate localDate = LocalDate.now();// For reference
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String date = localDate.format(formatter);

        LocalTime localTime = LocalTime.now();
        formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localTime.format(formatter);
        
        return new Notification(title, desc, date, time, path);
    }
}
