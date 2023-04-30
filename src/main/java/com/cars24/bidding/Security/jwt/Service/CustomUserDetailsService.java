package com.cars24.bidding.Security.jwt.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// for defining static userid and password forgenerating token
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username.equals("Admin")) {
            
            return new User("Admin","Admin",new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User Not Found");
        }
    }
}
