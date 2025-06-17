package com.example.web.controllers;

import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.request.UserRegisterRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserRegisterResponse;
import com.example.web.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserRegisterResponse register(
            @RequestBody() @Valid UserRegisterRequest userRegisterRequest
    ) {
        return userService.registerUser(userRegisterRequest);
    }

    @PostMapping("/data")
    public UserDataResponse getUserData(
            @RequestBody() @Valid UserLoginRequest userLoginRequest
    ) {
        return userService.getLoggedUserData(userLoginRequest);
    }
}
