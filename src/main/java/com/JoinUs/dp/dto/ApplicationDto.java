// src/main/java/com/JoinUs/dp/dto/ApplicationDto.java
package com.JoinUs.dp.dto;

import com.JoinUs.dp.entity.Application;
import com.JoinUs.dp.entity.ClubStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApplicationDto {

    private Long applicationId;
    private Long userId;
    private String clubId;
    private ClubStatus status;
    private String message;
    private LocalDateTime createdAt;

    public static ApplicationDto from(Application e) {
        return new ApplicationDto(
                e.getId(),
                e.getUserId(),
                String.valueOf(e.getClubId()),
                e.getStatus(),
                e.getMessage(),
                e.getCreatedAt()
        );
    }
}
