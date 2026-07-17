package com.han.community.controller;

import com.han.community.dto.NotificationDto;
import com.han.community.entity.User;
import com.han.community.dto.common.CursorResponse;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread-count")
    public ResponseEntity<SuccessResponse<NotificationDto.UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal User user) {

        NotificationDto.UnreadCountResponse response = notificationService.getUnreadNotificationCount(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @GetMapping
    public ResponseEntity<CursorResponse<NotificationDto.Response>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {

        CursorResponse<NotificationDto.Response> response = notificationService.getNotifications(user.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        notificationService.markRead(user.getId(), id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateAll(@AuthenticationPrincipal User user) {

        notificationService.markAllRead(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
