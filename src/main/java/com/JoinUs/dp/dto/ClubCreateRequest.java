package com.JoinUs.dp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClubCreateRequest {
    // 생성 시 요청 본문에서 받는 필드 전체
    private String id;                 // 응답 포맷 맞추기용, DB에서는 자동 생성
    private String name;
    private String shortDescription;
    private String description;
    private String type;               // general / major
    private String category;
    private String department;
    private String adminId;            // 현재는 사용하지 않음
    private String imageUrl;           // 대표 이미지 URL

    private Integer members;           // 초기/현재 인원
    private List<String> tags;         // 태그 목록 (엔티티 매핑 미사용)

    private Boolean isRecruiting;      // 모집 상태
    private String recruitDeadline;    // yyyy-MM-dd
    private List<String> activities;   // 활동 목록
    private String direction;          // 비전/방향

    private Long leaderId;
}
