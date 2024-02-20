package com.example.laba.controllers;

import com.example.laba.entities.Message;
import com.example.laba.entities.Users;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import com.example.laba.structures.MessageForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainPageController {
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    SecurityService securityService;


    @RequestMapping(value = {"/", "/public/index"}, method = { RequestMethod.GET, RequestMethod.POST })
    public String index(Model model) {

        List<Message> messages = messagesRepository.findAll();
        List<MessageForm> result = new ArrayList<>();

        for (Message mes : messages) {
            Users user = mes.getUser();
            result.add(new MessageForm(user.getId(), user.getLogin(), mes.getText()));
        }

        model.addAttribute("messages", result);

        return "public/main_page";
    }

    @PostMapping("/send")
    public void send(@RequestParam String text, HttpServletResponse response) {
        Message message = new Message();
        message.setUser(usersRepository.getReferenceById(securityService.getUserId()));
        message.setText(text);
        messagesRepository.save(message);

        response.setHeader("Location", "/public/index");
        response.setStatus(302);
    }

    @GetMapping("/public/photo/{id}")
    public ResponseEntity<byte[]> photo(@PathVariable long id) throws IOException {

        byte[] photo = usersRepository.getReferenceById(id).getPhoto();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .header("Content-Length", String.valueOf(photo.length))
                .body(photo);
    }
}