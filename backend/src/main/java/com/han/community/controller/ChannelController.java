package com.han.community.controller;

import com.han.community.dto.ChannelDto;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.service.ChannelService;
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
    public ResponseEntity<SuccessResponse<List<ChannelDto.Response>>> getChannels() {

        List<ChannelDto.Response> response = channelService.getChannels();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }
}
