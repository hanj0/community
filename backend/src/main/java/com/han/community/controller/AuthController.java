package com.han.community.controller;

import com.han.community.dto.AuthDto;
import com.han.community.dto.UserDto;
import com.han.community.entity.User;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    protected AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<AuthDto.Response>> signup(
            @RequestBody AuthDto.SignupRequest request,
            HttpServletRequest httpRequest) {

        User user = authService.signup(request);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = httpRequest.getSession(true);
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        AuthDto.Response response = AuthDto.Response.from(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthDto.Response>> login(
            @RequestBody AuthDto.LoginRequest request,
            HttpServletRequest httpRequest) {

        Authentication auth = authService.login(request);

        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = httpRequest.getSession(true);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        User user = (User)auth.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthDto.Response.from(user)));

    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<AuthDto.StatusResponse>> me(
            @AuthenticationPrincipal User user) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthDto.StatusResponse.from(user)));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> patchPassword(
            @AuthenticationPrincipal User user,
            @RequestBody AuthDto.ChangePasswordRequest requestDto) {

        authService.changePassword(user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
