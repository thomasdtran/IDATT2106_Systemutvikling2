package no.ntnu.idatt210620217.giddapi.controller;


import no.ntnu.idatt210620217.giddapi.Service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/participants")
@CrossOrigin
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    /**
     * Method for signing up for an activity. Calls method in service class.
     * @param activity_id of the activity to sign up on.
     * @param user_id of the user who want to sign up on the activity.
     * @return Http status code 200 if it works, else 400.
     */
    @PostMapping("/{activity_id}/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity signUpToActivity(@PathVariable long activity_id, @PathVariable long user_id){
        try {
            return participantService.signUpToActivity(activity_id, user_id);
        } catch (AccessException accessException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Method for signing off an activity. Calls method in service class.
     * @param activity_id id of the activity.
     * @param user_id id of the user.
     * @return Http status code 200 if it works, else 400.
     */
    @DeleteMapping("/{activity_id}/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> signOffActivity(@PathVariable long activity_id, @PathVariable long user_id){
        try {
            return participantService.signOffActivity(activity_id, user_id);
        } catch (AccessException accessException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
