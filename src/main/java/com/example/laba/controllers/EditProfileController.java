package com.example.laba.controllers;

import com.example.laba.entities.FUser;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.HandleImageService;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@Controller
public class EditProfileController {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;
    @Autowired
    SecurityService securityService;

    @Autowired
    HandleImageService handleImageService;

    @GetMapping("/user/edit_profile/{username}")
    String get_form_for_edit_profile(@PathVariable String username,
                                     Model model) {

        if (!Objects.equals(securityService.getUsername(), username)
                && !Objects.equals(securityService.getAccess(), "admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you can`t edit the profile of " + username);
        }

        TmplUser user;

        try {
            user = DAOService.get_user_by_login(username);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");
        }

        model.addAttribute("t_user", user);

        return "user/edit_profile";
    }

    @PostMapping("/user/edit_profile/{username}")
    void edit_profile(@RequestParam String sex,
                      @RequestParam String description,
                      @RequestParam MultipartFile file,
                      @PathVariable String username,
                      HttpServletResponse response) {

        if (!Objects.equals(securityService.getUsername(), username)
                && !Objects.equals(securityService.getAccess(), "admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you can`t edit the profile of " + username);
        }


        byte[] photo = {};
        if (!file.isEmpty()) {
            try {
                photo = handleImageService.cropping_scaling(file.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error during file processing");
            }
        }

        DAOService.update_user(username,
                (sex.equals("male"))? "мужской" : "женский", description, photo);

        response.setHeader("Location", "/public/profile/" + username);
        response.setStatus(302);
    }
}
