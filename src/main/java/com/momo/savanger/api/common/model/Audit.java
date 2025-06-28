package com.momo.savanger.api.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Audit {

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    @PrePersist
    private void prePersist() {
        createDate = LocalDateTime.now();
        updateDate = createDate;
        onPrePersist();
    }

    @PreUpdate
    private void preUpdate() {
        updateDate = LocalDateTime.now();
        onPreUpdate();
    }

    protected void onPrePersist() {
    }

    protected void onPreUpdate() {
    }

}
