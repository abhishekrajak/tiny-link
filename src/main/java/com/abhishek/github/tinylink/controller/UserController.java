package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.ApiResponse;
import com.abhishek.github.tinylink.dto.TinyLinkUserDTO;
import com.abhishek.github.tinylink.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Endpoints for managing user profiles and retrieving user account details")
public class UserController {

    private final UserService userService;

    @PostMapping("/demo")
    ApiResponse<?> generateDemoUser() {
        TinyLinkUserDTO newDemoUser = userService.createAndSaveDemoUser();

        if (newDemoUser != null){
            return ApiResponse.success(newDemoUser);
        } else {
            return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
        }
    }

}



