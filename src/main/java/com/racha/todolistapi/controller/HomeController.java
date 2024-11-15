package com.racha.todolistapi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "hello world";
    }
}
