package com.example.laba.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RulesController {
    @GetMapping("/public/rules")
    String get_rules_page(Model model) {
        return "public/rules_page";
    }
}
