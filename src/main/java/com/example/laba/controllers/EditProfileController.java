package com.example.laba.controllers;

import com.example.laba.entities.FUser;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Controller
public class EditProfileController {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;
    @Autowired
    SecurityService securityService;

    @GetMapping("/user/edit_profile")
    String get_form_for_edit_profile(Model model) {
        TmplUser user;

        try {
            user = DAOService.get_user_by_login(securityService.getUsername());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");
        }

        model.addAttribute("t_user", user);

        return "user/edit_profile";
    }

    @PostMapping("/user/edit_profile")
    void edit_profile(@RequestParam String sex,
                      @RequestParam String description,
                      @RequestParam MultipartFile file,
                      HttpServletResponse response) {
        byte[] photo = {};
        if (!file.isEmpty()) {
            try {
                photo = file.getBytes();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error during file processing");
            }
        }

        DAOService.update_user(securityService.getUsername(),
                (sex.equals("male"))? "мужской" : "женский", description, photo);

        response.setHeader("Location", "/public/profile/" + securityService.getUserId());
        response.setStatus(302);
    }
}
