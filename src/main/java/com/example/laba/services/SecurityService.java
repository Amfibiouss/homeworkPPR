package com.example.laba.services;

import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplUser;
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
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    public TmplUser getUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken)
            return null;

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) context.getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return DAOService.get_user_by_login(userDetails.getUsername());
    }

    public String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof AnonymousAuthenticationToken)
            return null;

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) context.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
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

    public boolean hasBan(String username) {

        if (username == null) {
            return false;
        }

        DAOService.update_punishments_status(username);

        return DAOService.has_ban(username);
    }

    public boolean hasUwU(String username) {

        if (username == null) {
            return false;
        }
        return DAOService.has_UwU(username);
    }
}
