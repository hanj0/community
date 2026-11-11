package com.han.community.channel.service;

import com.han.community.channel.dto.ChannelDto;
import com.han.community.channel.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    protected ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<ChannelDto.Response> getChannels() {

        return channelRepository.findAll().stream()
                .map(ChannelDto.Response :: from)
                .toList();
    }
}
