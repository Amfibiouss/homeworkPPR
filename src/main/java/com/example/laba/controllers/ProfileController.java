package com.example.laba.controllers;

import com.example.laba.entities.FUser;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class ProfileController {
    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/public/profile/{user_id}")
    String get_profile(Model model, @PathVariable long user_id) {

        Optional<FUser> opt_user = usersRepository.findById(user_id);

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

        return "public/profile_page";
    }
}
