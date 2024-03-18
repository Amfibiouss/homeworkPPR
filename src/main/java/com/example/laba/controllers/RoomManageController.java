package com.example.laba.controllers;

import com.example.laba.json_objects.*;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.CatMaidService;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import com.example.laba.services.SecurityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.aspectj.bridge.MessageUtil.getMessages;

@Controller
public class RoomManageController {

    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    SecurityService securityService;

    @Autowired
    CatMaidService catMaidService;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/user/exit_room/{room_id}")
    void exit_room(@PathVariable long room_id,
                   HttpServletResponse response) {

        if (!RCMDAOService.authorize_player(securityService.getUsername(), room_id, true)) {
            response.setHeader("Location", "/public/rooms");
            response.setStatus(302);
        }

        RCMDAOService.remove_player(securityService.getUsername(), true);

        template.convertAndSend("/topic/players/" + room_id,  RCMDAOService.get_players(room_id));

        response.setHeader("Location", "/public/rooms");
        response.setStatus(302);
    }

    @PostMapping("/user/start_room/{room_id}")
    void start_room(@PathVariable long room_id,
                    @RequestBody InputStateRoom inputStateRoom,
                    HttpServletResponse response,
                    ObjectMapper objectMapper) {

        if (RCMDAOService.set_room_status(securityService.getUsername(), room_id, "started")) {

            RCMDAOService.set_state(room_id, inputStateRoom);

            template.convertAndSend("/topic/room_status/" + room_id, "started");
        }
    }

    @PostMapping("/user/processing_start_room/{room_id}")
    @ResponseBody
    List<String> processing_start_room(@PathVariable long room_id,
                                            HttpServletResponse response,
                                            ObjectMapper objectMapper) {

        if (RCMDAOService.set_room_status(securityService.getUsername(), room_id, "processing")) {
            template.convertAndSend("/topic/room_status/" + room_id,  "processing");
        }

        return RCMDAOService.numerate_player(room_id);
    }

    @ResponseBody
    @GetMapping("/user/status_room/{room_id}")
    OutputStateRoom status_room(@PathVariable long room_id,
                                ObjectMapper objectMapper) {

        return RCMDAOService.get_state_for_player(room_id, securityService.getUsername());
    }
}
