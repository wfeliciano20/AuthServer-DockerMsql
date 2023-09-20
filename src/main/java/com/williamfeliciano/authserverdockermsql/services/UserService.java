package com.williamfeliciano.authserverdockermsql.services;

import com.williamfeliciano.authserverdockermsql.models.User;
import com.williamfeliciano.authserverdockermsql.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    // Need this to be able to interact with the DB to save or modify user
    private final UserRepository userRepository;
    // This is needed for the auth
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            }
        };
    }

    public User save(User newUser){
        if(newUser.getId() == null){
            newUser.setCreatedAt(LocalDateTime.now());
        }

        newUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(newUser);
    }
}
