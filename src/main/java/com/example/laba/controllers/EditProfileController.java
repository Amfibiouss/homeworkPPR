package com.example.laba.controllers;

import com.example.laba.entities.FUser;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.repositories.UsersRepository;
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
import java.util.Optional;

@Controller
public class EditProfileController {
    @Autowired
    SecurityService securityService;
    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/user/edit_profile")
    String get_form_for_edit_profile(Model model) {

        Optional<FUser> opt_user = usersRepository.findById(securityService.getUserId());

        if (opt_user.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");

        FUser user1 = opt_user.get();
        TmplUser user2 = new TmplUser();
        user2.setAdmin(user1.getAdmin());
        user2.setLogin(user1.getLogin());
        user2.setSex(user1.getSex());
        user2.setDescription(user1.getDescription());
        user2.setId(user1.getId());

        model.addAttribute("t_user", user2);

        return "user/edit_profile";
    }

    @PostMapping("/user/edit_profile")
    void edit_profile(@RequestParam String sex,
                      @RequestParam String description,
                      @RequestParam MultipartFile file,
                      HttpServletResponse response) {

        Optional<FUser> opt_user = usersRepository.findById(securityService.getUserId());

        if (opt_user.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");

        FUser user = opt_user.get();
        user.setSex((sex.equals("male"))? "мужской" : "женский");
        user.setDescription(description);

        if (!file.isEmpty()) {
            try {
                user.setPhoto(file.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error during file processing");
            }
        }
        usersRepository.save(user);

        response.setHeader("Location", "/public/profile/" + securityService.getUserId());
        response.setStatus(302);
    }
}
