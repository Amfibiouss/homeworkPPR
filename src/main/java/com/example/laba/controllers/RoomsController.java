package com.example.laba.controllers;

import com.example.laba.json_objects.OutputRoom;
import com.example.laba.json_objects.OutputRooms;
import com.example.laba.services.RoomChannelMessageDaoService;
import com.example.laba.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RoomsController {
    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Autowired
    SecurityService securityService;

    @GetMapping({"/public/rooms", "/"})
    String get_rooms_page(Model model) {

        List<OutputRoom> rooms = RCMDAOService.get_rooms();

        model.addAttribute("rooms", rooms);

        return "public/rooms_page";
    }

    @ResponseBody
    @GetMapping({"/public/get_rooms"})
    OutputRooms get_rooms() {
        return new OutputRooms(RCMDAOService.get_rooms(),
                RCMDAOService.get_current_room(securityService.getUsername()));
    }

}

