package com.han.community.controller;

import com.han.community.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    protected UserController(UserService userService) {
        this.userService = userService;
    }


}
