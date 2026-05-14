package com.han.community.controller;

import com.han.community.dto.PostDto;
import com.han.community.entity.User;
import com.han.community.service.PostService;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PostDto.Response> createPost(
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
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<Page<PostDto.Response>> getPostPage(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<PostDto.Response> page = postService.getPostPage(pageable);
        return ResponseEntity.ok(page);
    }
}
