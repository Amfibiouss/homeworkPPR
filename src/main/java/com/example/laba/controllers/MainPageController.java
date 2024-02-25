package com.example.laba.controllers;

import com.example.laba.entities.FMessage;
import com.example.laba.entities.FSection;
import com.example.laba.entities.FUser;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.SectionsRepository;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class MainPageController {
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    SectionsRepository sectionsRepository;

    @Autowired
    SecurityService securityService;


    @RequestMapping(value = {"/public/index/{section_id}"}, method = { RequestMethod.GET, RequestMethod.POST })
    public String index(Model model, @PathVariable long section_id) {

        Optional<FSection> section =  sectionsRepository.findById(section_id);

        if (section.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the section_id don't exist.");

        Collection<FMessage> messages = section.get().getMessages();
        List<TmplMessage> result = new ArrayList<>();

        for (FMessage mes : messages) {
            FUser user = mes.getUser();
            result.add(new TmplMessage(user.getId(), user.getLogin(), mes.getText()));
        }

        model.addAttribute("section", section_id);
        model.addAttribute("messages", result);

        return "public/main_page";
    }

    @PostMapping("/send/{section_id}")
    public void send(@RequestParam String text,
                     HttpServletResponse response,
                     @PathVariable long section_id) {
        FMessage FMessage = new FMessage();
        FMessage.setSection(sectionsRepository.getReferenceById(section_id));
        FMessage.setUser(usersRepository.getReferenceById(securityService.getUserId()));
        FMessage.setText(text);
        messagesRepository.save(FMessage);

        response.setHeader("Location", "/public/index/" + section_id);
        response.setStatus(302);
    }

    @GetMapping("/public/photo/{user_id}")
    public ResponseEntity<byte[]> photo(@PathVariable long user_id) throws IOException {

        Optional<FUser> user = usersRepository.findById(user_id);

        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");

        byte[] photo = user.get().getPhoto();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .header("Content-Length", String.valueOf(photo.length))
                .body(photo);
    }
}