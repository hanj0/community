package com.han.community.controller;

import com.han.community.dto.AuthDto;
import com.han.community.entity.User;
import com.han.community.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// 회원가입, 로그인, 로그아웃

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    protected AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthDto.Response> signup(@RequestBody @Valid AuthDto.SignupRequest request) {

        AuthDto.Response response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(
            @RequestBody @Valid AuthDto.LoginRequest request,
            HttpServletRequest httpRequest) {

        Authentication auth = authService.login(request);

        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = httpRequest.getSession(true);

        session.invalidate();
        session = httpRequest.getSession(true);
        SecurityContextHolder.getContext().setAuthentication(auth);


        User user = (User)((UsernamePasswordAuthenticationToken)auth).getPrincipal();
        return ResponseEntity.ok(AuthDto.Response.from(user));
    }


}
