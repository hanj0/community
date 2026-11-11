package com.han.community.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseSoftDeleteEntity extends BaseEntity {

    @Column
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
