package com.han.community.channel.service;

import com.han.community.channel.dto.ChannelResponse;
import com.han.community.channel.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    protected ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<ChannelResponse> getChannels() {

        return channelRepository.findAll().stream()
                .map(ChannelResponse:: from)
                .toList();
    }
}
