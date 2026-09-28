package com.han.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseSoftDeleteEntity extends BaseEntity {

    @Column
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
