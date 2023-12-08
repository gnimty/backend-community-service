package com.gnimty.communityapiserver.domain.base.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @NotNull
    @Column(name = "deleted", columnDefinition = "TINYINT default 0")
    protected Boolean deleted = false;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    protected LocalDateTime createdAt;

    @NotNull
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    protected LocalDateTime updatedAt;

    public void delete() {
        deleted = true;
    }
}
