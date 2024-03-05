package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @GetMapping("/register")
    String get_registration() {
        return "public/registration_page";
    }

    @PostMapping("/register")
    void register(@RequestParam String username,
                  @RequestParam String password,
                  HttpServletResponse response) {

        DAOService.add_user(username, password, false);

        response.setHeader("Location", "/public/login");
        response.setStatus(302);
    }

}
