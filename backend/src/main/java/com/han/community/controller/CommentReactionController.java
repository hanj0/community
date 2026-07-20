package com.han.community.controller;

import com.han.community.dto.CommentReactionDto;
import com.han.community.entity.User;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.service.CommentReactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentReactionController {

    private CommentReactionService commentReactionService;

    @PutMapping("/{commentId}/reaction")
    public ResponseEntity<SuccessResponse<Void>> putReaction(
            @PathVariable Long commentId,
            @RequestBody CommentReactionDto.Request requestDto,
            @AuthenticationPrincipal User user) {

        commentReactionService.reactToComment(commentId, user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{commentId}/reaction")
    public ResponseEntity<Void> deleteRecation(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {

        commentReactionService.deleteReaction(commentId, user.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
