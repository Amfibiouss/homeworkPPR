package com.example.laba.controllers;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Controller
public class PunishController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    SecurityService securityService;

    @PostMapping("/admin/punish")
    void punish(HttpServletResponse response,
                @RequestHeader("Referer") String referer,
                @RequestParam(required = false) String rule1,
                @RequestParam(required = false) String rule2,
                @RequestParam(required = false) String rule3,
                @RequestParam(required = false) String rule4,
                @RequestParam(required = false) String rule5,
                @RequestParam(required = false) String UwU,
                @RequestParam String username,
                @RequestParam String description) {

        try {

            List<String> violations = Arrays.asList(rule1, rule2, rule3, rule4, rule5, UwU);
            int cnt = 1;

            DAOService.update_punishments_status(username);

            for (String violation : violations) {
                if (violation != null) {
                    DAOService.try_punish(username, cnt, description);
                }
                cnt++;
            }

            response.setHeader("Location", referer);
            response.setStatus(302);

        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
