package com.han.community.post.viewcount;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class ViewerIdResolver {

    private final String COOKIE_NAME = "vid";

    public String resolve(HttpServletRequest req, HttpServletResponse res, Long userId) {

        if(userId != null) return "u:" + userId;

        String uuid = readCookies(req);
        if(uuid != null) return "g:" + uuid;

        issueCookies(res);
        return null;
    }

    private String readCookies(HttpServletRequest req) {

        if(req.getCookies() == null) return null;

        for(Cookie c : req.getCookies()) {
            if(c.getName().equals(COOKIE_NAME)) {
                try {
                    return UUID.fromString(c.getValue()).toString();
                } catch(IllegalArgumentException e) {
                    return null;
                }
            }
        }

        return null;
    }

    private void issueCookies(HttpServletResponse res) {

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, UUID.randomUUID().toString())
                .httpOnly(true)
                //.secure(true) // todo: https로 변경시 적용
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(24))
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
