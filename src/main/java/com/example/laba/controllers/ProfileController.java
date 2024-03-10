package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Controller
public class ProfileController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    SecurityService securityService;

    @GetMapping("/public/profile/{username}")
    String get_profile(Model model, @PathVariable String username) {
        try {
            TmplUser user = DAOService.get_user_by_login(username);
            model.addAttribute("t_user", user);

            if (Objects.equals(securityService.getAccess(), "admin")) {
                DAOService.update_punishments_status(username);
                List<TmplPunishment> punishments = DAOService.get_punishments(username, true);

                if (DAOService.has_UwU(username)) {
                    TmplPunishment punishment = new TmplPunishment();
                    punishment.setId(-1);
                    punishment.setRule(6L);
                    punishment.setDescription("UwU");
                    punishment.setUsername(username);

                    punishments.add(punishment);
                }

                model.addAttribute("punishments", punishments);
            }
            return "public/profile_page";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
