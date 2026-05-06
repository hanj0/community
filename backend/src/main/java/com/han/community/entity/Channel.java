package com.han.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Channel {

    @Id
    private Long id;

    private String name;
    private String description;
    private String slug;
}
