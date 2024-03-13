package com.example.laba.controllers;

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

    @GetMapping("/public/photo/{username}")
    public ResponseEntity<byte[]> photo(
            HttpServletRequest request,
            @PathVariable String username) throws IOException {

        long curr_etag = DAOService.get_eTag(username);

        if (Objects.equals(request.getHeader("If-None-Match"), "\"" + String.valueOf(curr_etag) + "\"")) {

            return ResponseEntity.status(304).body(null);
        }

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
                    .header("ETag", String.valueOf(curr_etag))
                    .header("Cache-control",  "must-revalidate")
                    .body(photo);
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the user_id don't exist.");
        }
    }
}