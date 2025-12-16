package com.JoinUs.dp.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewRequest {

    private Long applicationId;
    private String clubId;
    private Long userId;

    private LocalDateTime schedule;
    private String location;
    private String memo;
}
