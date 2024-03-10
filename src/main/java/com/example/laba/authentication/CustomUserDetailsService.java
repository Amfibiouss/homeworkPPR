package com.example.laba.authentication;

import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            TmplUser user = DAOService.get_user_by_login(username);
            String password = DAOService.get_password(username);

            if (user.getAdmin()) {
                return User.builder()
                        .username(username)
                        .password(password)
                        .roles("USER", "ADMIN")
                        .build();
            }
            else {
                return User.builder()
                        .username(username)
                        .password(password)
                        .roles("USER")
                        .build();
            }

        } catch (Exception e) {
            throw new UsernameNotFoundException("username " + username + " not found");
        }
    }
}
