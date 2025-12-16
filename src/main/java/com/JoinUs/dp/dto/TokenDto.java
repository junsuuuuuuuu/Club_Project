package com.JoinUs.dp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
