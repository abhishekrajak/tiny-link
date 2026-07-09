package com.abhishek.github.tinylink.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@Tag(name = "User Details", description = "Endpoints for managing user profiles and retrieving user account details")
public class UserDetailsController {}



