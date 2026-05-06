package com.han.community.dto;

import com.han.community.entity.Channel;
import lombok.Builder;

public class ChannelDto {

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
