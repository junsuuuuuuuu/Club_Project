package com.JoinUs.dp.dto;

import com.JoinUs.dp.entity.ClubStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApplicationResponse {

    private Long applicationId;
    private String clubId;
    private String clubName;
    private ClubStatus status;       // 단일 상태
    private String message;          // 심사/확정 메시지
    private LocalDateTime createdAt;
}
