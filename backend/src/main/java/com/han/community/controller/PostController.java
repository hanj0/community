package com.han.community.controller;

import com.han.community.dto.PostDto;
import com.han.community.entity.User;
import com.han.community.global.response.PageResponse;
import com.han.community.global.response.SuccessResponse;
import com.han.community.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostDto.DetailResponse>> getPostDetail(@PathVariable Long id) {

        PostDto.DetailResponse response = postService.getDetail(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<PostDto.Response>> createPost(
            @RequestBody PostDto.CreateRequest requestDto,
            @AuthenticationPrincipal User user) {

        PostDto.Response response = postService.create(requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostDto.Response>> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto.UpdateRequest requestDto,
            @AuthenticationPrincipal User user) {

        PostDto.Response response = postService.update(id, requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {

        postService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostDto.Response>> getPostPage(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false, defaultValue = "") String search) {

        Page<PostDto.Response> response = postService.getPostPage(channelId, search, sort, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<PageResponse<PostDto.Response>> getHotPostPage(
            @RequestParam(defaultValue = "24h") String period,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostDto.Response> response = postService.getHotPostPage(period, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }
}
