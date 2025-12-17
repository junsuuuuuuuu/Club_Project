// src/main/java/com/JoinUs/dp/entity/Application.java
package com.JoinUs.dp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Applications")
@Getter
@Setter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "club_id", nullable = false)
    private String clubId;

    @Enumerated(EnumType.STRING)
    private ClubStatus status = ClubStatus.PENDING;   // 통합된 상태

    private String message;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
