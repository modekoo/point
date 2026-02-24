package com.point.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class DateEntity {
    @CreatedDate
    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt = LocalDateTime.now();
}