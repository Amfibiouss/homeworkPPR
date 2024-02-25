package com.example.laba.services;

import com.example.laba.entities.FUser;
import com.example.laba.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<FUser> users = usersRepository.findByLogin(username);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("username " + username + " not found");
        }

        FUser user = users.getFirst();

        if (user.getAdmin()) {
            return User.withDefaultPasswordEncoder()
                    .username(username)
                    .password(users.getFirst().getPassword())
                    .roles("USER", "ADMIN")
                    .build();
        }
        else {
            return User.withDefaultPasswordEncoder()
                    .username(username)
                    .password(users.getFirst().getPassword())
                    .roles("USER")
                    .build();
        }
    }
}
