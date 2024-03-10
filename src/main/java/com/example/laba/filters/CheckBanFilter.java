package com.example.laba.filters;

import com.example.laba.services.SecurityService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
@Order(100)
public class CheckBanFilter implements Filter {

    @Autowired
    SecurityService securityService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        HttpServletResponse res = (HttpServletResponse) response;

        try {
            if (securityService.hasBan(securityService.getUsername())) {
                res.setHeader("Location", "/logout");
                res.setStatus(302);
                return;
            }
        } catch (Exception e) {
            res.setStatus(400);
            return;
        }

        chain.doFilter(request, response);
    }
}
