package com.han.community.controller;

import com.han.community.dto.PostDto;
import com.han.community.dto.UserDto;
import com.han.community.entity.User;
import com.han.community.global.response.PageResponse;
import com.han.community.global.response.SuccessResponse;
import com.han.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me/stats")
    public ResponseEntity<SuccessResponse<UserDto.StatsResponse>> getStats(@AuthenticationPrincipal User user) {

        UserDto.StatsResponse response = userService.getStats(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @GetMapping("/me/posts")
    public ResponseEntity<PageResponse<PostDto.SummaryResponse>> getMyPosts(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal User user) {

        Page<PostDto.SummaryResponse> response = userService.getMyPosts(user.getId(), pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/me/comments")
    public ResponseEntity<PageResponse<UserDto.MyCommentResponse>> getMyComments(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal User user) {

        Page<UserDto.MyCommentResponse> response = userService.getMyComments(user.getId(), pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<PageResponse<PostDto.SummaryResponse>> getMyBookmarks(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal User user) {

        Page<PostDto.SummaryResponse> response = userService.getMyBookmarks(user.getId(), pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }
}
