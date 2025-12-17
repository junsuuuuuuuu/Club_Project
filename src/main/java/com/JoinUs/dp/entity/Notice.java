package com.JoinUs.dp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notices")
@Getter
@NoArgsConstructor
public class Notice {

    public enum Type {
        FAQ,
        CLUB_NOTICE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(name = "club_id")
    private String clubId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public Notice(String clubId, String title, String content, Type type) {
        this.clubId = clubId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.type = type;
    }
}
