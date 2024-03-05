package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplSection;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddSectionController {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;
    @Autowired
    SecurityService securityService;
    @PostMapping("/user/add_section")
    void add_section(@RequestParam String section_name,
                     @RequestParam String section_description,
                     HttpServletResponse response) {

        TmplSection section = new TmplSection();
        section.setName(section_name);
        section.setDescription(section_description);
        section.setCreator(securityService.getUsername());

        DAOService.add_section(section);

        response.setHeader("Location", "/public/index/" + section.getId());
        response.setStatus(302);
    }
}
