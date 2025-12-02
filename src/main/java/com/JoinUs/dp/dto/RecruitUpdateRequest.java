package com.JoinUs.dp.dto;

import lombok.Data;

/**
 * 모집 상태 + 마감일 변경용 DTO
 * {
 *   "isRecruiting": true,
 *   "recruitDeadline": "2025-11-30"
 * }
 */
@Data
public class RecruitUpdateRequest {
    private Boolean isRecruiting;      // true : open, false : closed
    private String recruitDeadline;    // "yyyy-MM-dd" 또는 null
}
