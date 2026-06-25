package com.han.community.controller;

import com.han.community.entity.User;
import com.han.community.service.BookmarkService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PutMapping("/posts/{postId}/bookmark")
    public ResponseEntity<Void> putBookmark(@PathVariable Long postId,
                                            @AuthenticationPrincipal User user) {

        bookmarkService.setBookmark(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/posts/{postId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long postId,
                                               @AuthenticationPrincipal User user) {

        bookmarkService.deleteBookmark(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
