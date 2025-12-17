package com.JoinUs.dp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clubs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club {

    @Id
    @Column(name = "club_id")
    private String clubId;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_desc", nullable = false)
    private String shortDesc;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String activities;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(nullable = false)
    private String type;   // enum('major','general')

    private String department;

    private String category;

    @Column(nullable = false, columnDefinition = "enum('pending','approved')")
    private String status = "pending";

    @Column(name = "recruit_status", nullable = false, columnDefinition = "enum('open','closed')")
    private String recruitStatus = "closed";

    @Column(name = "recruitment_notice", columnDefinition = "TEXT")
    private String recruitmentNotice;

    @Column(name = "recruitment_start_date")
    private java.sql.Date recruitmentStartDate;

    @Column(name = "recruitment_end_date")
    private java.sql.Date recruitmentEndDate;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;
    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "recruiting")
    private Boolean recruiting;
}
