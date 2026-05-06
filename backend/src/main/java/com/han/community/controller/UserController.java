package com.han.community.controller;

import com.han.community.dto.UserDto;
import com.han.community.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// 마이페이지 조회, 내정보 수정, 탈퇴


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    protected UserController(UserService userService) {
        this.userService = userService;
    }


}
