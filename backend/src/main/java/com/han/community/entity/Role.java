package com.han.community.entity;

public enum Role {

    USER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
