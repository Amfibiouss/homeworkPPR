package com.example.laba.services;

import com.example.laba.objects_to_fill_templates.TmplUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

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

        try {
            TmplUser user = DAOService.get_user_by_login(userDetails.getUsername());
            return user.getId();
        } catch (Exception e) {
            return 0;
        }
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
