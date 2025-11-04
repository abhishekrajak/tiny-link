package com.abhishek.github.tinylink.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

    @GetMapping("/error")
    String getError () {
        // TODO handle this
        return "YOU HAVE REACHED THE ERROR PAGE";
    }
}
