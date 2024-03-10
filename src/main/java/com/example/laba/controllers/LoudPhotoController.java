package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class LoudPhotoController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @GetMapping("/public/photo/{username}")
    public ResponseEntity<byte[]> photo(@PathVariable String username) throws IOException {

        try {
            byte[] photo = DAOService.get_photo(username);

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