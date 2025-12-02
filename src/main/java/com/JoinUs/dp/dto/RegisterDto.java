package com.JoinUs.dp.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String email;
    private String password;
    private String username;

    // 아래부터는 DB NOT NULL 대응용 필드
    private String nickname;     // 없으면 username 재사용
    private String department;   // 미입력 시 "미정"
    private String studentId;    // 미입력 시 임시값 생성
    private Integer grade;       // 미입력 시 1
}
