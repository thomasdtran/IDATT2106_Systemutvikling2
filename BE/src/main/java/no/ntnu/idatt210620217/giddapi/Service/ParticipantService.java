package no.ntnu.idatt210620217.giddapi.Service;

import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.model.Participant;
import no.ntnu.idatt210620217.giddapi.model.ParticipantId;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.ParticipantRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * Service class for Participant
 */
@Service
public class ParticipantService {

    @Autowired
    ActivityRepo activityRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ParticipantRepo participantRepo;

    /** Method for signing up on an activity. Checks if user is already signed up for the activity or not.
     * @param activity_id id of the activity.
     * @param user_id id of the user.
     * @return Http status code 200 if it works, else 400.
     */
    public ResponseEntity signUpToActivity(long activity_id, long user_id) throws AccessException {
        Activity activity = activityRepo.findById(activity_id);
        User user = userRepo.findById(user_id);
        System.out.println(user.toString());

        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != user_id)
            throw new AccessException("Cannot add equipment if authenticated user is not organizer of activity");

        //Checks if user is participant or not
        boolean isParticipant = isParticipant(activity, user);

        //Tries to add the user from the activity if it is not a participant.
        if (!isParticipant) {
            try {
                // Checks if there are more available spaces in activity
                if (activity.getParticipants().size() < activity.getMaxParticipants()){
                    System.out.println("participants: " + activity.getParticipants().size() + " max: " + activity.getMaxParticipants());
                    Participant participant = new Participant(user, activity, false);
                    participantRepo.save(participant);
                } else {
                    // checks if person already is on the waitinglist
                    if (!isOnWaitinglist(activity, user)){
                        activity.getWaitlist().add(user);
                        activity = activityRepo.save(activity);
                        System.out.println("list size: " + activity.getWaitlist().size());
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                }
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for updating waitlist when someone signs off an activity
     * @param activity_id Id of activity to update waitlist for
     */
    private void updateWaitlist(long activity_id){
        Activity activity = activityRepo.findById(activity_id);
        System.out.println("updates WAITLIST");
        // If the waitlist is empty there are no participants to bump up
        if (!activity.getWaitlist().isEmpty()){
            try{
                User user = activity.getWaitlist().stream().findFirst().get();
                activity.getWaitlist().remove(user);
                activityRepo.save(activity);
                System.out.println("add user: " + user.getId() + " to participants");
                signUpToActivity(activity_id, user.getId());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * Method for signing off an activity. Checks if user is signed up for the activity or not.
     * @param activity_id id of the activity.
     * @param user_id id of the user.
     * @return Http status code 200 if it works, else 400.
     */
    public ResponseEntity signOffActivity(long activity_id, long user_id) throws AccessException {
        Activity activity = activityRepo.findById(activity_id);
        User user = userRepo.findById(user_id);

        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != user_id)
            throw new AccessException("Cannot add equipment if authenticated user is not organizer of activity");

        //System.out.println(participant.getUser().getEmail());
        //Checks if user is participant or not
        boolean isParticipant = isParticipant(activity, user);

        // checks if user is on waitinglist
        boolean isWaiting = isOnWaitinglist(activity, user);
        //Tries to remove the user from the activity if it is a participant.
        if (isParticipant || isWaiting) {
            try {
                // removes user from activity if the user is already on waitinglist
                if (isWaiting){
                    activity.getWaitlist().remove(user);
                    user.getMyWaitlists().remove(activity);
                    userRepo.save(user);
                    activityRepo.save(activity);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                //Participant participant = participantRepo.findByActivityIdAndUserId(activity.getId(), user.getId());
                //participantRepo.delete(participant);
                //updateWaitlist(activity_id);
                Participant participant = getParticipantFromActivity(activity, user.getId());
                activity.getParticipants().remove(participant);
                activityRepo.save(activity);
                updateWaitlist(activity_id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for telling if user is on the activity's waiting list
     * @param activity Activity object
     * @param user User object
     * @return A boolean value telling if user is on the activity's waiting list
     */
    private boolean isOnWaitinglist(Activity activity, User user){
        for (User checkUser : activity.getWaitlist()) {
            if (checkUser.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for telling if user is a participant of an activity
     * @param activity Activity object
     * @param user User object
     * @return A boolean value telling if user is a participant of an activity
     */
    private boolean isParticipant(Activity activity, User user){
        Set<Participant> participants = activity.getParticipants();
        for(Participant participant : participants) {
            if(participant.getUser().getId() == user.getId()) return true;
        }
        return false;
    }

    /**
     * Method for fetching a user from an activity
     * @param activity Activity object
     * @param userId Id of a user
     * @return Returns a participant from a certain activity
     */
    private Participant getParticipantFromActivity(Activity activity, long userId) {
        for(Participant participant : activity.getParticipants()) {
            if(participant.getUser().getId() == userId) return participant;
        }
        return null;
    }
}
