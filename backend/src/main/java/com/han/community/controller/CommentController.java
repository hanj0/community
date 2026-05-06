package com.han.community.controller;

import com.han.community.dto.CommentDto;
import com.han.community.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentDto.Response>> getComments(
            @PathVariable Long postId,
            @PageableDefault(sort = "create_at") Pageable pageable
    ) {

        Page<CommentDto.Response> response = commentService.getComments(postId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto.Response> create(@RequestBody CommentDto.CreateRequest dto) {

        CommentDto.Response response = commentService.create(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDto.Response> update(@RequestBody CommentDto.UpdateRequest dto) {

        CommentDto.Response response = commentService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
