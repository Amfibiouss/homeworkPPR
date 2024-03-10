package com.example.laba.authentication;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException authException)
            throws IOException, ServletException {

        if (authException instanceof LockedException) {
            response.sendRedirect("/public/login?error="
                    + URLEncoder.encode(authException.getMessage(), StandardCharsets.UTF_8));
            return;
        }
        if (authException instanceof BadCredentialsException) {
            response.sendRedirect("/public/login?error="
                    + URLEncoder.encode(authException.getMessage(), StandardCharsets.UTF_8));
            return;
        }

        response.setHeader("Location", "/public/login");
        response.setStatus(302);

    }
}
