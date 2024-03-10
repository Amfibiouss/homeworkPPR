package com.example.laba.controllers;

import com.example.laba.json_objects.InputMessage;
import com.example.laba.json_objects.OutputMessage;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
public class MessageController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    SecurityService securityService;

    @MessageMapping("/message/{section_id}")
    @SendTo("/topic/messages/{section_id}")
    public OutputMessage send(SimpMessageHeaderAccessor accessor,
                              InputMessage message,
                              @DestinationVariable long section_id) {

        if (accessor.getUser() == null || securityService.hasBan(accessor.getUser().getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only users can send messages");
        }

        try {
            DAOService.add_message(new OutputMessage(accessor.getUser().getName(), message.getText()), section_id);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user or the section don't exist");
        }

        return new OutputMessage(accessor.getUser().getName(), message.getText());
    }

    @MessageExceptionHandler
    void AuthorizationExceptionHandler(ResponseStatusException exception) {}

    @GetMapping("/public/chat/{section_id}")
    String get_chat(@PathVariable long section_id, Model model) {
        model.addAttribute("section_id", section_id);
        return "public/chat_page";
    }

    @ResponseBody
    @GetMapping("/public/chat/get_messages/{section_id}")
    List<OutputMessage> get_messages(@PathVariable long section_id) {
        try {
            return DAOService.get_messages(section_id, 0, 1_000_000);
        } catch(ServiceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the section don't exist");
        }
    }
}
