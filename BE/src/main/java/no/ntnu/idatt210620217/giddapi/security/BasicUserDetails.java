package no.ntnu.idatt210620217.giddapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Class for BasicUserDetails
 */
public class BasicUserDetails implements UserDetails {
    private String password;
    private String username;
    private long id;

    /**
     * Constructor for a BasicUserDetails
     * @param password
     * @param username
     * @param id
     */
    public BasicUserDetails(String password, String username, long id) {
        this.password = password;
        this.username = username;
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return username;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
