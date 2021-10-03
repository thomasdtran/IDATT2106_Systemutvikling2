package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.model.AuthenticationRequest;
import no.ntnu.idatt210620217.giddapi.model.AuthenticationResponse;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import no.ntnu.idatt210620217.giddapi.security.JwtUtil;
import no.ntnu.idatt210620217.giddapi.security.MyUserDetailsService;
import no.ntnu.idatt210620217.giddapi.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    @Autowired
    private AuthenticationManager authenticationmanager;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserService userService;


    Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * PostMapping for creating user
     * @param user RequestBody from client
     * @return user and HttpStatus.CREATED or HttpStatus.BAD_REQUEST
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User user1 = userService.createUser(user);
            logger.info("User was created");
            return new ResponseEntity<>(user1, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * GetMapping for finding user by id
     * @param id PathVariable from client
     * @return user and HttpStatus.OK or HttpStatus.BAD_REQUEST
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findUserById(@PathVariable long id) {
        try {
            Object user = userService.findUserById(id);
            logger.info("Found user with id: " + id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * GetMapping for getting list of specific user info
     * @return list with specific user info and HttpStatus.OK if all ok or BAD_REQUEST
     */
    @GetMapping
    public ResponseEntity<?> getUserAndTotalPoints() {
        return new ResponseEntity<>(userService.getUsersAndTotalPoints(), HttpStatus.OK);
    }

    /**
     * PutMapping for updating user info
     * @param user, Requesting Body with new user info
     * @param id, users id, PathVariable from client
     * @return user and HttpStatus.OK if all ok or BAD_REQUEST if wrong input from user
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable long id) {
        try {
            User updatedUser = userService.updateUser(user, id);
            logger.info("Updated user with id: " + id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * DeleteMapping for deleting a user
     * @param id, PathVariable which is the users id
     * @return
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            logger.info("Deleted user with id: " + id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Post mapping to authorise a user and get an authorisation-token in return
     * @param authenticationRequest request body containing email and password
     * @return the token used to authorise access in later requests
     * @throws Exception
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationmanager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password");
        }

        BasicUserDetails userDetails = (BasicUserDetails) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt, userDetails.getId()));
    }

    /**
     * Method for confirming if password sent by user is equal to password in database
     * @param id id og user sending password
     * @param user The user sending the password
     * @return 200 if ok, 400 if not
     */
    @PostMapping("/{id}/confirm-password")
    public ResponseEntity<?> confirmPassword(@PathVariable long id, @RequestBody User user) {
        if(userService.confirmPassword(id, user.getPassword()))
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * @param user User object
     * @return returns and error code of ok if password is changed
     */
    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody User user) {
        try {
            userService.recoverPassword(user.getEmail());
            logger.info("Password changed for user");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Password could not change for user");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
