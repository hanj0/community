package com.han.community.controller;

import com.han.community.dto.CommentDto;
import com.han.community.entity.User;
import com.han.community.global.response.PageResponse;
import com.han.community.global.response.SuccessResponse;
import com.han.community.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentDto.Response>> getComments(
            @PathVariable Long postId,
            @PageableDefault(sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal User user
    ) {

        Long userId = user == null ? null : user.getId();
        Page<CommentDto.Response> response = commentService.getComments(postId, userId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<PageResponse<CommentDto.Response>> getReplies(
            @PathVariable Long commentId,
            @PageableDefault(sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal User user
    ) {

        Long userId = user == null ? null : user.getId();
        Page<CommentDto.Response> response = commentService.getReplies(commentId, userId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<SuccessResponse<CommentDto.Response>> create(
            @RequestBody CommentDto.CreateRequest requestDto,
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        CommentDto.Response response = commentService.create(requestDto, postId, user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<SuccessResponse<CommentDto.Response>> update(
            @PathVariable Long id,
            @RequestBody CommentDto.UpdateRequest requestDto,
            @AuthenticationPrincipal User user) {

        CommentDto.Response response = commentService.update(id, requestDto, user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {

        commentService.delete(id, user.getId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
