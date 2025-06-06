package com.example.web.controllers;

import com.example.web.model.user.request.UserRegisterRequest;
import com.example.web.model.user.response.UserDataResponse;
import com.example.web.model.user.response.UserRegisterResponse;
import com.example.web.service.UserService;
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
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return userService.getUserData(username, password);
    }
}
