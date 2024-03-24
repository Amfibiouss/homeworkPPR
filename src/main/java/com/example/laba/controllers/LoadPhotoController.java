package com.example.laba.controllers;

import com.example.laba.services.HandleImageService;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Objects;

@Controller
public class LoadPhotoController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    HandleImageService handleImageService;

    @GetMapping("/public/photo/{username}")
    public ResponseEntity<byte[]> photo(
            HttpServletRequest request,
            @PathVariable String username) throws IOException {


        if (Character.isDigit(username.charAt(0))) {
            int number = Integer.parseInt(username);
            byte[] photo = handleImageService.create_image(
                    127*(number % 3),
                    127*((number/3) % 3),
                    127*((number/9) % 3));

            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .header("Content-Length", String.valueOf(photo.length))
                    .body(photo);
        }

        long curr_etag = DAOService.get_eTag(username);

        if (Objects.equals(request.getHeader("If-None-Match"), "\"" + String.valueOf(curr_etag) + "\"")) {

            return ResponseEntity.status(304).body(null);
        }

        try {

            byte[] photo = DAOService.get_photo(username);

            if (photo == null) {
                Resource resource = new ClassPathResource("static/public_static/who_i_am.jpg");

                try (InputStream inputStream = new FileInputStream(resource.getFile())) {
                    photo = new byte[(int) resource.contentLength()];
                    inputStream.read(photo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .header("Content-Length", String.valueOf(photo.length))
                    .header("ETag", String.valueOf(curr_etag))
                    .header("Cache-control",  "must-revalidate")
                    .body(photo);
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user don't exist.");
        }
    }
}