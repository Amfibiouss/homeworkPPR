package com.example.laba.authentication;

import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();


        UserDetails detailsFromDAO;

        try {
            detailsFromDAO = userDetailsService.loadUserByUsername(username);

            if (!Objects.equals(detailsFromDAO.getPassword(), password))
                throw new BadCredentialsException("invalid username or password");

        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("invalid username or password");
        }

        DAOService.update_punishments_status(username);

        if (DAOService.has_ban(username)) {
            List <TmplPunishment> punishments = DAOService.get_punishments(username, true);

            OffsetDateTime date_finish = null;

            for (TmplPunishment punishment : punishments) {
                if (date_finish == null || punishment.getDate_finish().isAfter(date_finish)) {
                    date_finish =  punishment.getDate_finish();
                }
            }

            throw new LockedException("user have been banned before " + date_finish);
        }

        //System.out.println( new UsernamePasswordAuthenticationToken(detailsFromDAO, "").se);

        return new UsernamePasswordAuthenticationToken(detailsFromDAO, "", detailsFromDAO.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}