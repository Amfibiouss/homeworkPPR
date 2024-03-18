package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.max;

@Controller
public class SearchMessageController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @GetMapping("/admin/search_message/{username}/{page}")
    String get_search_message(@PathVariable String username,
                              @PathVariable long page,
                              Model model) {

        try {
            model.addAttribute("t_user", DAOService.get_user_by_login(username));
            model.addAttribute("messages", RCMDAOService.get_messages_of_user(username, 5 * (page - 1), 5));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the username don't exist");
        }

        List<Long> pages = new ArrayList<>();

        for (long i = max(page - 4, 1L); i < page + 4; i++) {
            pages.add(i);
        }

        model.addAttribute("pages", pages);


        return "/admin/search_message_page";
    }

}
