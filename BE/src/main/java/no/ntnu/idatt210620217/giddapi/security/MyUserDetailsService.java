package no.ntnu.idatt210620217.giddapi.security;

import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for loading userDetails
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    /**
     * Method for getting userDetails by username
     * @param s username
     * @return userDetail
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findUserByEmail(s);
        if(user == null)
            throw new UsernameNotFoundException("No user with username found");
        return new BasicUserDetails(user.getPassword(), user.getEmail(), user.getId());
    }
}