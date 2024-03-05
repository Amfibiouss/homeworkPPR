package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class MainPageController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    SecurityService securityService;


    @RequestMapping(value = {"/public/index/{section_id}"}, method = { RequestMethod.GET, RequestMethod.POST })
    public String index(Model model, @PathVariable long section_id) {
        try {
            List<TmplMessage> messages = DAOService.get_messages(section_id, 0, 1000000);

            model.addAttribute("section", section_id);
            model.addAttribute("messages", messages);

            return "public/main_page";

        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the section_id don't exist.");
        }
    }

    @PostMapping("/send/{section_id}")
    public void send(@RequestParam String text,
                     HttpServletResponse response,
                     @PathVariable long section_id) {

        try {
            DAOService.add_message(
                    new TmplMessage(securityService.getUserId(), securityService.getUsername(), text),
                    section_id);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the section_id don't exist.");
        }

        response.setHeader("Location", "/public/index/" + section_id);
        response.setStatus(302);
    }

    @GetMapping("/public/photo/{user_id}")
    public ResponseEntity<byte[]> photo(@PathVariable long user_id) throws IOException {

        try {
            byte[] photo = DAOService.get_photo(user_id);

            if (photo == null) {
                Resource resource = new ClassPathResource("static/Дефолт.jpg");

                try(InputStream inputStream = new FileInputStream(resource.getFile())){
                    photo = new byte[(int)resource.contentLength()];
                    inputStream.read(photo);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .header("Content-Length", String.valueOf(photo.length))
                    .body(photo);
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");
        }
    }
}