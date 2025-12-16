package com.JoinUs.dp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WishlistRequest {
    @NotNull(message = "클럽 ID는 필수입니다.")
    private String clubId;
}
