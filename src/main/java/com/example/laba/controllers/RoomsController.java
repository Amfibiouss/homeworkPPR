package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplRoom;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RoomsController {
    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @GetMapping({"/public/rooms", "/"})
    String get_rooms(Model model) {

        List<TmplRoom> rooms = RCMDAOService.get_rooms();

        model.addAttribute("rooms", rooms);

        return "public/rooms_page";
    }
}

