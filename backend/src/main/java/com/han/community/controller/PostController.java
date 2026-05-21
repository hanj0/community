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
    public ResponseEntity<PostDto.Response> getPostDetail(@PathVariable Long id) {

        PostDto.Response response = postService.getDetail(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<PostDto.Response>> createPost(
            @RequestBody PostDto.CreateRequest requestDto,
            @AuthenticationPrincipal User user) {

        System.out.println(
                user.getUsername() + user.getPassword() +
                        user.getAuthorities() +
                        user.getCreatedAt() +
                        user.getId() +
                        user.getEmail()
        );
        PostDto.Response response = postService.create(requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(@RequestBody PostDto.UpdateRequest req) {

        PostDto.Response response = postService.update(req);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostDto.Response>> getPostPage(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "latest") String sortBy) {

        Sort sort = switch(sortBy) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable finalPageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                sort
        );

        Page<PostDto.Response> page = postService.getPostPage(finalPageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(page));
    }
}
