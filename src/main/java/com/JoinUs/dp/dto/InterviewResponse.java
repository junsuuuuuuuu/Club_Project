package com.JoinUs.dp.dto;

import com.JoinUs.dp.entity.Interview;
import com.JoinUs.dp.entity.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InterviewResponse {

    private Long interviewId;
    private Long applicationId;
    private String clubId;
    private Long userId;

    private LocalDateTime schedule;
    private String location;

    private InterviewStatus status;
    private String memo;

    private LocalDateTime createdAt;

    public static InterviewResponse from(Interview i) {
        return new InterviewResponse(
                i.getId(),
                i.getApplicationId(),
                String.valueOf(i.getClubId()),
                i.getUserId(),
                i.getSchedule(),
                i.getLocation(),
                i.getStatus(),
                i.getMemo(),
                i.getCreatedAt()
        );
    }
}
