// (선택) 부가 응답: 학과별 클럽 목록 요약
// src/main/java/com/JoinUs/dp/global/domain/application/dto/ClubSummary.java
package com.JoinUs.dp.dto;

public class ClubSummary {
    private String clubId;
    private String clubName;

    public ClubSummary() {}
    public ClubSummary(String clubId, String clubName) {
        this.clubId = clubId;
        this.clubName = clubName;
    }
    public String getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public void setClubId(String clubId) { this.clubId = clubId; }
    public void setClubName(String clubName) { this.clubName = clubName; }
}
