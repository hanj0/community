package com.han.community.channel.dto;

import com.han.community.channel.entity.Channel;

public record ChannelResponse (

    Long id,
    String name,
    String description
) {

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription()
        );
    }
}
