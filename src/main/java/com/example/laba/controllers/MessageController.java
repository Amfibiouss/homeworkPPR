package com.example.laba.controllers;

import com.example.laba.json_objects.InputMessage;
import com.example.laba.json_objects.OutputMessage;
import com.example.laba.objects_to_fill_templates.TmplChannel;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.CatMaidService;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Controller
public class MessageController {

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

    @MessageMapping("/message/{room_id}/{channel_id}")
    @SendTo("/topic/messages/{room_id}/{channel_id}")
    public String send(SimpMessageHeaderAccessor accessor,
                              InputMessage message,
                              @DestinationVariable long room_id,
                              @DestinationVariable long channel_id) {

        if (accessor.getUser() == null || securityService.hasBan(accessor.getUser().getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only users can send messages");
        }

        String username = accessor.getUser().getName();

        if (!RCMDAOService.authorize_player(username, room_id, false)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only players can send messages");
        }

        String text = message.getText();

        //if (DAOService.get_last_messages())

        if (securityService.hasUwUStage(username, 1)) {
            text = catMaidService.addCatMaidAccent(text);
        }

        if (RCMDAOService.too_much_messages(username)) {
            template.convertAndSend("/topic/user/" +  channel_id + "/" + username,
                    new OutputMessage("Admin", "Не отправляй так часто сообщения.",0));
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "you send messages too often.");
        }


        long message_id;
        try {
            message_id = RCMDAOService.add_message(username, text, channel_id, -1);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user or the section don't exist");
        }

        return String.valueOf(message_id);
    }

    @GetMapping("/public/chat/{room_id}")
    String get_chat(
            @PathVariable long room_id,
            HttpServletResponse response,
            Model model) {

        //model.addAttribute("lobby_id", RCMDAOService.get_channel_id(room_id, "лобби"));
        model.addAttribute("room_id", room_id);

        RCMDAOService.almost_add_player(securityService.getUsername(), room_id);

        if (RCMDAOService.isHost(room_id, securityService.getUsername())) {
            model.addAttribute("host", true);
        } else {
            model.addAttribute("host", false);
        }

        return "public/chat_page";
    }

    @ResponseBody
    @GetMapping("/public/chat/get_messages/{room_id}/{channel_id}")
    String get_messages(
            @PathVariable long room_id,
            @PathVariable long channel_id) {

        if (!RCMDAOService.authorize_player(securityService.getUsername(), room_id, true)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only players can send messages");
        }

        try {
            return RCMDAOService.get_messages(channel_id, securityService.getUsername());
        } catch(ServiceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the channel don't exist");
        }
    }

    @ResponseBody
    @GetMapping("/public/chat/get_stage_messages/{channel_id}/{stage_id}")
    String get_stage_messages(
            @PathVariable long stage_id,
            @PathVariable long channel_id) {

        try {
            return RCMDAOService.get_stage_messages(channel_id, stage_id, securityService.getUsername());
        } catch(ServiceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bed parameters", exception);
        }
    }

    @ResponseBody
    @GetMapping("/public/chat/get_message/{message_id}")
    String get_message(
            @PathVariable long message_id) {

        try {

            return RCMDAOService.get_message(message_id, securityService.getUsername());

        } catch (ServiceException e) {
            if (Objects.equals(e.getMessage(), "the message_id don't exist."))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the message_id don't exist.");

            if (Objects.equals(e.getMessage(), "user are not unauthorized to read the data."))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user are not unauthorized to read the data.");
        } catch (Exception e) {
            System.out.println(e);
        }

        throw new RuntimeException();//IDE меня заставило это написать, очевидно не будет никогда вызвано.
    }

    @ResponseBody
    @GetMapping("/public/room/get_players/{room_id}")
    List<TmplUser> get_players(@PathVariable long room_id) {
        if (!RCMDAOService.authorize_player(securityService.getUsername(), room_id, true)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only players can send messages");
        }

        try {
            return RCMDAOService.get_players(room_id);
        } catch(ServiceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the room don't exist");
        }
    }
}
