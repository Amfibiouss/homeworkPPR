package com.example.laba.services;

import com.example.laba.entities.FUser;
import com.example.laba.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("securityService")
public class SecurityService {

    @Autowired
    UsersRepository usersRepository;

    public String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken)
            return null;

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) context.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    public long getUserId() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken)
            return 0;

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) context.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<FUser> res = usersRepository.findByLogin(userDetails.getUsername());
        if (res.isEmpty())
            return 0;
        return res.getFirst().getId();
    }

    public String getAccess() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken)
            return "public";

        if (context.getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "admin";
        } else {
            return "user";
        }
    }
}
