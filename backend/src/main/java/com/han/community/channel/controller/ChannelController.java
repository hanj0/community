package com.han.community.channel.controller;

import com.han.community.channel.dto.ChannelResponse;
import com.han.community.global.dto.SuccessResponse;
import com.han.community.channel.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ChannelResponse>>> getChannels() {

        List<ChannelResponse> response = channelService.getChannels();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }
}
