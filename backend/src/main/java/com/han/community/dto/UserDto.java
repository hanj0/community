package com.han.community.dto;

import com.han.community.entity.User;
import lombok.Builder;
import lombok.Getter;


public class UserDto {

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String username;

        public static Response from(User user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .build();
        }
    }
}
