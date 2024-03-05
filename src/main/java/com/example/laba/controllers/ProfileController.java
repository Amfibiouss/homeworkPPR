package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProfileController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @GetMapping("/public/profile/{user_id}")
    String get_profile(Model model, @PathVariable long user_id) {
        try {
            TmplUser user = DAOService.get_user(user_id);
            model.addAttribute("t_user", user);
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return "public/profile_page";
    }
}
