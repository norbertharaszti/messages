package edu.progmatic.messageapp.services;

import edu.progmatic.messageapp.modell.RegisteredUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    Map<String, RegisteredUser> users = new HashMap<>();

    public UserService() {
        createUser(new RegisteredUser("admin", "password", "a@a.a", LocalDate.of(2000,1,1),"ROLE_ADMIN"));
        createUser(new RegisteredUser("user", "password", "a@a.a", LocalDate.of(2000,1,1),"ROLE_USER"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }

    public void createUser(RegisteredUser user) {
        users.put(user.getUsername(), user);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
