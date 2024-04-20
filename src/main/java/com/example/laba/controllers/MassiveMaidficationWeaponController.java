package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.http.HttpResponse;

@Controller
public class MassiveMaidficationWeaponController {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;
    @GetMapping("/user/launch_massive_maidfication_weapon")
    String get_massive_maidfication_weapon() {
        return "admin/MMW";
    }

    @PostMapping("/user/launch_massive_maidfication_weapon")
    void launch_massive_maidfication_weapon(HttpServletResponse response) {
        DAOService.make_massive_maidfication();
        response.setHeader("Location", "/user/launch_massive_maidfication_weapon");
        response.setStatus(302);
    }
}
