package com.youngpotato.firsttoyprojectback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "cre_time", nullable = false)
    private LocalDateTime creTime;

    @CreatedBy
    @Column(name = "cre_email", nullable = false)
    private String creEmail;

    @LastModifiedDate
    @Column(name = "upd_time", nullable = false)
    private LocalDateTime updTime;

    @LastModifiedBy
    @Column(name = "upd_email")
    private String updEmail;

//    abstract public String name();
}
