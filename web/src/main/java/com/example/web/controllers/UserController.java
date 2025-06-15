package com.example.web.controllers;

import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.request.UserRegisterRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserRegisterResponse;
import com.example.web.service.user.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserRegisterResponse register(
            @RequestBody() @Validated UserRegisterRequest userRegisterRequest
    ) {
        return userService.registerUser(userRegisterRequest);
    }

    @PostMapping("/data")
    public UserDataResponse getUserData(
            @RequestBody() @Validated UserLoginRequest userLoginRequest
    ) {
        return userService.getLoggedUserData(userLoginRequest);
    }
}
