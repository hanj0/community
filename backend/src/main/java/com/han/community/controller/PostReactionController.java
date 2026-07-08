package com.han.community.controller;

import com.han.community.dto.PostReactionDto;
import com.han.community.entity.User;
import com.han.community.global.response.SuccessResponse;
import com.han.community.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PutMapping("/{postId}/reaction")
    public ResponseEntity<SuccessResponse<Void>> putReaction(
            @PathVariable Long postId,
            @RequestBody PostReactionDto.Request requestDto,
            @AuthenticationPrincipal User user) {

        postReactionService.reactToPost(postId, user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{postId}/reaction")
    public ResponseEntity<Void> deleteReaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        postReactionService.deleteReaction(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
