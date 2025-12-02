package com.JoinUs.dp.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubListResponse {

    private String id;                 // "sg01"
    private String name;
    private String type;
    private String category;
    private String department;

    private String adminId;            // "sg_lead"

    private String shortDescription;
    private String description;

    private String imageUrl;

    private Integer members;
    private List<String> tags;

    private Boolean isRecruiting;
    private String recruitDeadline;

    private List<String> activities;
    private String direction;

    private List<NoticeSummary> notices;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeSummary {

        private String id;     // "sg-notice-1"
        private String title;
        private String content;
        private String date;   // yyyy-MM-dd
        private boolean isImportant;
    }
}
