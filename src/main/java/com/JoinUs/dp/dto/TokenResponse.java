package com.JoinUs.dp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private Long userId;
    private String access_token;
    private String refresh_token;
}
