package com.example.laba.controllers;

import com.example.laba.entities.FSection;
import com.example.laba.objects_to_fill_templates.TmplSection;
import com.example.laba.repositories.SectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SectionsController {

    @Autowired
    SectionsRepository sectionsRepository;

    @GetMapping({"/public/sections", "/"})
    String get_section(Model model) {

        List<FSection> sections = sectionsRepository.findAll();
        List<TmplSection> result = new ArrayList<>();

        for (FSection section : sections) {
            result.add(new TmplSection(section.getId(), section.getCreator().getLogin(),
                                        section.getName(), section.getDescription()));
        }

        model.addAttribute("sections", result);

        return "public/sections_page";
    }
}

