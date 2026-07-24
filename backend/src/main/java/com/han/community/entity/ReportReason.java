package com.han.community.entity;

public enum ReportReason {

    SPAM,
    ABUSE,
    SEXUAL,
    ILLEGAL,
    ETC;

    public boolean requiresDetail() {
        return this == ETC;
    }
}
