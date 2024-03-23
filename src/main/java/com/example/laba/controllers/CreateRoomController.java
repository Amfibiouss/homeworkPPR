package com.example.laba.controllers;

import com.example.laba.json_objects.InputRoom;
import com.example.laba.objects_to_fill_templates.TmplRoom;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import com.example.laba.services.SecurityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    @GetMapping("/user/load_room")
    ResponseEntity<String> load_room(HttpServletRequest request) {

        HttpSession session = request.getSession();
        StringBuffer room_config = (StringBuffer) session.getAttribute("room_config");

        if (room_config == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(new String(room_config));
    }

    @PostMapping("/user/add_room")
    void add_room(@RequestParam MultipartFile file,
                  HttpServletResponse response,
                  ObjectMapper objectMapper) {

        InputRoom room = null;
        try {
            room = objectMapper.readValue(file.getInputStream(), InputRoom.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long room_id = RCMDAOService.add_room(room, securityService.getUsername());

        response.setHeader("Location", "/public/chat/" + room_id);
        response.setStatus(302);
    }
}
