package com.example.laba;

import com.example.laba.entities.Messages;
import com.example.laba.entities.Users;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import com.example.laba.structures.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class MainPageController {

    @Value("classpath:static/chat.json")
    Resource chat;

    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/index")
    public String index(Model model) {

        List<Messages> messages = messagesRepository.findAll();
        List<Message> result = new ArrayList<>();

        for (Messages mes : messages) {
            Users user = mes.getUser();
            result.add(new Message(user.getId(), user.getLogin(), mes.getText()));
        }

        model.addAttribute("messages", result);

        return "main_page";
    }

    @PostMapping("/send")
    public void send(@RequestParam String text, HttpServletResponse response) {
        Messages message = new Messages();
        message.setUser(usersRepository.getReferenceById(4L));
        message.setText(text);
        messagesRepository.save(message);

        response.setHeader("Location", "/index");
        response.setStatus(302);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> photo(@PathVariable long id) throws IOException {

        byte[] photo = usersRepository.getReferenceById(id).getPhoto();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .header("Content-Length", String.valueOf(photo.length))
                .body(photo);
    }
}