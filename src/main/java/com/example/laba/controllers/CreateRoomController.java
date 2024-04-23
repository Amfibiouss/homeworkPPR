package com.example.laba.controllers;

import com.example.laba.json_objects.InputRoom;
import com.example.laba.services.RoomChannelMessageDaoService;
import com.example.laba.services.SecurityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CreateRoomController {
    @Autowired
    SecurityService securityService;

    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @GetMapping("/user/create_room")
    String get_create_room() {
        return "user/create_room";
    }

    @PostMapping("/user/create_room")
    void create_room(HttpServletResponse response,
                     HttpServletRequest request,
                     @RequestBody InputRoom room,
                     ObjectMapper objectMapper) {

        HttpSession session = request.getSession();
        try {
            String json_string = objectMapper.writeValueAsString(room);
            session.setAttribute("room_config", new StringBuffer(json_string));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatus(200);
    }

    @PostMapping("/user/add_room")
    void add_room(@RequestParam String room_name,
                  @RequestParam String room_description,
                  @RequestParam String room_help,
                  @RequestParam long room_min_players,
                  @RequestParam long room_max_players,
                  @RequestParam String mode,
                  HttpServletResponse response,
                  ObjectMapper objectMapper) {

        if (mode.equals("debug")) {
            room_min_players = room_max_players = 1;
        }
        if (mode.equals("avengers")) {
            room_min_players = room_max_players = 12;
        }

        InputRoom room = new InputRoom(room_name, room_description, room_help,
                room_min_players, room_max_players, mode);

        long room_id = RCMDAOService.add_room(room, securityService.getUsername());

        response.setHeader("Location", "/public/chat/" + room_id);
        response.setStatus(302);
    }

    @PostMapping("/user/add_recent_room")
    void add_recent_room(HttpServletResponse response) {

        InputRoom room = RCMDAOService.getUserRecentRoom(securityService.getUsername());

        if (room == null) {
            response.setHeader("Location", "/public/rooms");
            response.setStatus(302);
            return;
        }

        long room_id = RCMDAOService.add_room(room, securityService.getUsername());

        response.setHeader("Location", "/public/chat/" + room_id);
        response.setStatus(302);
    }

    @PostMapping("/user/add_room_by_string")
    void add_room_by_string(HttpServletResponse response) {

        InputRoom room = RCMDAOService.getUserRecentRoom(securityService.getUsername());

        if (room == null) {
            response.setHeader("Location", "/public/rooms");
            response.setStatus(302);
            return;
        }

        long room_id = RCMDAOService.add_room(room, securityService.getUsername());

        response.setHeader("Location", "/public/chat/" + room_id);
        response.setStatus(302);
    }
}
