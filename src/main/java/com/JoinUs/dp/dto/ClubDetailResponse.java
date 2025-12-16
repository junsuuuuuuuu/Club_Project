package com.JoinUs.dp.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubDetailResponse {

    private String clubId;
    private String name;
    private String shortDesc;
    private String description;
    private String type;
    private String department;
    private String category;
    private String recruitStatus;
    private List<String> images;
}
