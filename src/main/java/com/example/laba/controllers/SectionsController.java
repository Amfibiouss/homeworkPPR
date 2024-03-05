package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplSection;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SectionsController {
    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @GetMapping({"/public/sections", "/"})
    String get_section(Model model) {

        List<TmplSection> sections = DAOService.get_sections();

        model.addAttribute("sections", sections);

        return "public/sections_page";
    }
}

