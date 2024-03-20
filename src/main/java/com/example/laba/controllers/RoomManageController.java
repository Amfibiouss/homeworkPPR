package com.example.laba.controllers;

import com.example.laba.json_objects.*;
import com.example.laba.services.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class RoomManageController {

    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Autowired
    SecurityService securityService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    TimerService timerService;

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
                    HttpServletResponse response) {

        if (RCMDAOService.set_room_status(securityService.getUsername(), room_id, "started")) {

            RCMDAOService.set_state(room_id, inputStateRoom, true);
            timerService.notify_host_after_delay(room_id, inputStateRoom.duration);

            template.convertAndSend("/topic/room_status/" + room_id, "started");
        }

        response.setStatus(200);
    }

    @PostMapping("/user/processing_start_room/{room_id}")
    @ResponseBody
    List<String> processing_start_room(@PathVariable long room_id,
                                            HttpServletResponse response) {

        if (RCMDAOService.set_room_status(securityService.getUsername(), room_id, "processing")) {
            template.convertAndSend("/topic/room_status/" + room_id,  "processing");
        }

        return RCMDAOService.numerate_player(room_id);
    }

    @GetMapping("/user/status_room/{room_id}")
    @ResponseBody
    OutputStateRoom status_room(@PathVariable long room_id){

        return RCMDAOService.get_state_for_player(room_id, securityService.getUsername());
    }

    @GetMapping("/user/get_data_for_processing/{room_id}")
    @ResponseBody
    List<OutputPollResult> get_results_of_stage(@PathVariable long room_id) {

        return RCMDAOService.get_polls_results(room_id);
    }

    @PostMapping("/user/update_room_state/{room_id}")
    void set_state_of_stage(@PathVariable long room_id,
                            @RequestBody InputStateRoom inputStateRoom,
                            HttpServletResponse response) {

        if (RCMDAOService.set_room_status(securityService.getUsername(), room_id, "started")) {

            RCMDAOService.set_state(room_id, inputStateRoom, false);
            timerService.notify_host_after_delay(room_id, inputStateRoom.duration);

            template.convertAndSend("/topic/room_status/" + room_id, "started");
        }

        response.setStatus(200);
    }

    @PostMapping("/user/vote/{poll_id}")
    void vote(@PathVariable long poll_id,
              @RequestParam long candidate,
              HttpServletResponse response) {

        boolean success_vote = RCMDAOService.add_vote(poll_id, securityService.getUsername(), candidate);

        response.setStatus(200);
    }
}
