package no.ntnu.idatt210620217.giddapi.Service;

import no.ntnu.idatt210620217.giddapi.emailUtil.EmailService;
import no.ntnu.idatt210620217.giddapi.model.Rating;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import no.ntnu.idatt210620217.giddapi.security.PasswordGenerator;
import no.ntnu.idatt210620217.giddapi.security.Sha256PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.AccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for user service
 */
@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;
    @Value("${security.salt}")
    String salt;



    /**
     * Method for creating user
     * @param user data received from client to create user
     * @return user created or throw exception if input is wrong
     */
    public User createUser(User user) {
        user.setPassword(hashPassword(user.getPassword()));

        if(!isLetters(user.getFirstname()) || !isLetters(user.getLastname()) || !isLetters(user.getCity())) {
            throw new IllegalArgumentException("Firstname, lastname or city did not contain only letters");
        } else if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new IllegalArgumentException("Email must contain @ and .");
        } else if (String.valueOf(user.getBirthyear()).length() != 4) {
            throw new IllegalArgumentException("Wrong format for birthday");
        } else if (!isValidGender(user.getGender())) {
            throw new IllegalArgumentException("Wrong format for gender");
        }
        if (user.getRating() == null){
            Rating rating = new Rating(user);
            user.setRating(rating);
        }
        return userRepo.save(user);
    }

    /**
     * Method for finding user by id
     * @param id you want to use to find user
     * @return user or null if user does not exist
     */
    public Object findUserById(long id) throws AccessException {
        if(userRepo.findById(id) == null) {
            throw new NoSuchElementException("No such user with " + id + " exist");
        }

        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != id)
            throw new AccessException("Cannot request user different than the authenticated user");

        return userRepo.findById(id).getPrivateUser();
    }

    /**
     * A method for updating a user, contains checks for invalid input
     * @param newUserData, new data recieved from user
     * @param id, id of the user sending data
     * @return User object containing the new info
     */
    public User updateUser(User newUserData, long id) throws AccessException {
        User current = userRepo.findById(id);

        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != id)
            throw new AccessException("Cannot update user different than the authenticated user");

        if ((newUserData.getFirstname() != null) && (!(newUserData.getFirstname().equals(current.getFirstname())))) {
            if (isLetters(newUserData.getFirstname())) {
                current.setFirstname(newUserData.getFirstname());
            } else {
                throw new IllegalArgumentException("Firstname did not contain only letters");
            }
        }
        if ((newUserData.getLastname() != null) && !(newUserData.getLastname().equals(current.getLastname()))) {
            if (isLetters(newUserData.getLastname())) {
                current.setLastname(newUserData.getLastname());
            } else {
                throw new IllegalArgumentException("Lastname did not contain only letters");
            }
        }
        if ((newUserData.getBirthyear() != 0) && (newUserData.getBirthyear() != current.getBirthyear())) {
            if (String.valueOf(newUserData.getBirthyear()).length() == 4) {
                current.setBirthyear(newUserData.getBirthyear());
            } else {
                throw new IllegalArgumentException("Wrong format for birthday");
            }
        }
        if ((newUserData.getEmail() != null) && (!(newUserData.getEmail().equals(current.getEmail())))) {
            if (newUserData.getEmail().contains("@") && newUserData.getEmail().contains(".")) {
                current.setEmail(newUserData.getEmail());
            } else {
                throw new IllegalArgumentException("Email must contain @ and .");
            }
        }
        if ((newUserData.getGender() != null) && (!(newUserData.getGender().equals(current.getGender())))) {
            if (isValidGender(newUserData.getGender())) {
                current.setGender(newUserData.getGender());
            } else {
                throw new IllegalArgumentException("Wrong format for gender");
            }
        }
        //TODO password security
        if ((newUserData.getPassword() != null) && ((!hashPassword(newUserData.getPassword()).equals(current.getPassword())))) {
            current.setPassword(hashPassword(newUserData.getPassword()));
        }
        if ((newUserData.getCity() != null) && (!(newUserData.getCity().equals(current.getCity())))) {
            if (isLetters(newUserData.getCity())) {
                current.setCity(newUserData.getCity());
            } else {
                throw new IllegalArgumentException("City did not contain only letters");
            }

        }
        return userRepo.save(current);
    }

    /**
     * Method for deleting a user
     * @param id, of the user that is to be deleted
     */
    public void deleteUser(long id) throws AccessException {
        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != id)
            throw new AccessException("Cannot delete user different than the authenticated user");

        if(userRepo.findById(id) == null) {
            throw new NoSuchElementException("No such user with " + id + " exist");
        } else {
            userRepo.deleteById(id);
        }
    }

    /**
     * Method for extracting specific user info
     * @return list containing string arrays with users firstname, lastname and total points
     */
    public List<String[]> getUsersAndTotalPoints() {
        List<User> allUsers = userRepo.findAll();
        List<String[]> userAndTotaltPoints = new ArrayList<>();
        for(User user : allUsers) {
            String[] nameAndTotalPoints = new String[3];
            nameAndTotalPoints[0] = user.getFirstname();
            nameAndTotalPoints[1] = user.getLastname();
            nameAndTotalPoints[2] = String.valueOf(user.getTotalPoints());
            userAndTotaltPoints.add(nameAndTotalPoints);
        }
        userAndTotaltPoints.sort(Comparator.comparing(a -> Integer.parseInt(a[2])));
        Collections.reverse(userAndTotaltPoints);

        return userAndTotaltPoints;
    }

    /**
     * Method for checking if password is equal to password in database
     * @param id id of user that sends in password
     * @param password password sent in
     * @return boolean if password is correct or not
     */
    public boolean confirmPassword(long id, String password) {
        User user = userRepo.findById(id);
        if(user == null) return false;

        return hashPassword(password).equals(user.getPassword());
    }

    /**
     * Method used in createUser and updateUser to check if gender input is valid
     * @param gender, a String recieved from user
     * @return boolean, saying if it the input is valid
     */
    public boolean isValidGender(String gender) {
        String genders = "kvinne mann annet";
        String[] gendersSplit = genders.split(" ");

        for (String word : gendersSplit) {
            if (gender.toLowerCase().equals(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method used to check if input from user contains only letters
     * @param name, name recieved from user
     * @return boolean saying if the input is valid
     */
    public boolean isLetters(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(Character.isLetter(c) || c ==' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for recovering password of a user. New pw is sent to users email
     * @param email Email of a user
     */
    public void recoverPassword(String email){
        User user =  userRepo.findUserByEmail(email);
        if (user == null) throw new NullPointerException("User with this email was not found");
        String newPassword = PasswordGenerator.generatePassword();
        user.setPassword(hashPassword(newPassword));
        userRepo.save(user);

        // sends email to user
        emailService.sendEmail(email, "Gidd! varsler", "Forandringer med din Gidd! bruker\n" +
                "Ditt nye passord er: " + newPassword);
    }

    /**
     * A method to hash strings.
     * @param toHash the string that should be hashed
     * @return the hashed string
     */
    private String hashPassword(String toHash) {
        PasswordEncoder passwordEncoder = new Sha256PasswordEncoder(salt);
        return passwordEncoder.encode(toHash);
    }
}