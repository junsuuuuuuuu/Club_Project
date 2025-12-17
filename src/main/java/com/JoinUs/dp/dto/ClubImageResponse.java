package com.JoinUs.dp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubImageResponse {

    private Long imageId;
    private String imageUrl;
    private String uploadedAt;
}
