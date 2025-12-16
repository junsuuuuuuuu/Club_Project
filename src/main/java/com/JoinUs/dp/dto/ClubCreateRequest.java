package com.JoinUs.dp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClubCreateRequest {
    private String name;
    private String shortDescription;
    private String description;
    private String type;               // general / major
    private String category;
    private String department;
    private Long leaderId;

    private Boolean isRecruiting;      // 모집 상태
    private String recruitDeadline;    // yyyy-MM-dd
    private Integer members;           // 초기 인원
    private List<String> activities;   // 활동 목록
    private String direction;          // 비전/방향
}
