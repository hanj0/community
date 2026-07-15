package com.han.community.controller;

import com.han.community.dto.PostDto;
import com.han.community.entity.User;
import com.han.community.dto.common.PageResponse;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostDto.DetailResponse>> getPostDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

/// redis로 배치작업, 중복조회방지 리팩토링
        String cookieName = "vp_" + id;
        boolean alreadyViewed = Arrays.stream(
                        httpRequest.getCookies() != null ? httpRequest.getCookies() : new Cookie[0])
                .anyMatch(c -> c.getName().equals(cookieName));

        Long userId = user == null ? null : user.getId();
        PostDto.DetailResponse response = postService.getDetail(id, userId, alreadyViewed);

        if(!alreadyViewed) {
            Cookie cookie = new Cookie(cookieName, "true");
            cookie.setMaxAge(24*60*60);
            cookie.setPath("/");
            httpResponse.addCookie(cookie);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<PostDto.SummaryResponse>> createPost(
            @RequestBody PostDto.CreateRequest requestDto,
            @AuthenticationPrincipal User user) {

        PostDto.SummaryResponse response = postService.create(requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostDto.SummaryResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto.UpdateRequest requestDto,
            @AuthenticationPrincipal User user) {

        PostDto.SummaryResponse response = postService.update(id, requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
                                           @AuthenticationPrincipal User user) {

        postService.delete(id, user.getId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostDto.SummaryResponse>> getPostPage(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false, defaultValue = "") String search) {

        Page<PostDto.SummaryResponse> response = postService.getPostPage(channelId, search, sort, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<PageResponse<PostDto.SummaryResponse>> getHotPostPage(
            @RequestParam(defaultValue = "24h") String period,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostDto.SummaryResponse> response = postService.getHotPostPage(period, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }
}
