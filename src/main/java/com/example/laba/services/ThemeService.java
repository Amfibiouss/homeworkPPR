package com.example.laba.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("themeService")
public class ThemeService {
    @Autowired
    HttpServletRequest request;

    public String getTheme() {

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("theme") && cookie.getValue().equals("black"))
                return "black";
            if (cookie.getName().equals("theme") && cookie.getValue().equals("white"))
                return "white";
        }

        return "black";
    }
}
