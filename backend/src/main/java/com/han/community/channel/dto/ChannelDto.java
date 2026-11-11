package com.han.community.channel.dto;

import com.han.community.channel.entity.Channel;
import lombok.Builder;
import lombok.Getter;

public class ChannelDto {

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String name;
        private String description;

        public static Response from(Channel channel) {
            return Response.builder()
                    .id(channel.getId())
                    .name(channel.getName())
                    .description(channel.getDescription())
                    .build();
        }
    }
}
