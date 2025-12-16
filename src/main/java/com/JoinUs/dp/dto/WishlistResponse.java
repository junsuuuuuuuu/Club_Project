package com.JoinUs.dp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishlistResponse {

    private String clubId;
    private String clubName;
    private String type;        // general / major
    private String category;    // 일반동아리 카테고리
    private String department;  // 전공동아리 학과
}
