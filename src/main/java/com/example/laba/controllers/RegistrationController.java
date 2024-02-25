package com.example.laba.controllers;

import com.example.laba.entities.FUser;
import com.example.laba.repositories.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class RegistrationController {

    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/register")
    String get_registration() {
        return "public/registration_page";
    }

    @PostMapping("/register")
    void register(@RequestParam String username,
                  @RequestParam String password,
                  HttpServletResponse response) {

        FUser user = new FUser();
        user.setLogin(username);
        user.setPassword(password);
        user.setAdmin(false);

        Resource resource = new ClassPathResource("static/Админ.jpg");

        try(InputStream inputStream = new FileInputStream(resource.getFile())){
            byte[] photo = new byte[(int)resource.contentLength()];
            inputStream.read(photo);
            user.setPhoto(photo);
        } catch(IOException e) {
            e.printStackTrace();
        }

        usersRepository.save(user);

        response.setHeader("Location", "/public/login");
        response.setStatus(302);
    }

}
