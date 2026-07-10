package com.abhishek.github.tinylink.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@SecurityRequirement(name = "BearerAuth")
@Hidden
@Tag(name = "User Details Controller", description = "Endpoints for managing user profiles and retrieving user account details")
public class UserDetailsController {}



