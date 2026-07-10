package com.abhishek.github.tinylink.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@Tag(name = "Error Controller", description = "Endpoints for handling default error behavior and system error redirection")
public class ErrorController {

    @Operation(
            summary = "Get fallback error message",
            description = "Returns a standard fallback message when an unexpected error occurs or an invalid endpoint is accessed."
    )
    @GetMapping("/error")
    String getError () {
        // TODO handle this
        return "YOU HAVE REACHED THE ERROR PAGE";
    }
}
