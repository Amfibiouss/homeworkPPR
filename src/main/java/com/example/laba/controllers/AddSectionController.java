package com.example.laba.controllers;

import com.example.laba.entities.FSection;
import com.example.laba.repositories.SectionsRepository;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;

@Controller
public class AddSectionController {
    @Autowired
    SectionsRepository sectionsRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    SecurityService securityService;
    @PostMapping("/user/add_section")
    void add_section(@RequestParam String section_name,
                     @RequestParam String section_description,
                     HttpServletResponse response) {

        FSection section = new FSection();
        section.setName(section_name);
        section.setDescription(section_description);
        section.setCreator(usersRepository.getReferenceById(securityService.getUserId()));
        section.setMessages(new HashSet<>());

        sectionsRepository.save(section);

        response.setHeader("Location", "/public/index/" + section.getId());
        response.setStatus(302);
    }
}
