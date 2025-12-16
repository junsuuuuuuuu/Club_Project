package com.JoinUs.dp.dto;

import com.JoinUs.dp.global.common.api.ApiPath;
import com.JoinUs.dp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 * POST /api/v1/auth/register
 * POST /api/v1/auth/login
 * POST /api/v1/auth/refresh
 */
@RestController
@RequestMapping(ApiPath.AUTH_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<Response<Long>> register(@RequestBody RegisterDto dto) {
        Long userId = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(HttpStatus.CREATED, userId, "회원가입이 완료되었습니다."));
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<Response<TokenResponse>> login(@RequestBody LoginDto dto) {
        var tokens = authService.login(dto.getEmail(), dto.getPassword());
        TokenResponse body = new TokenResponse(tokens.getUserId(), tokens.getAccessToken(), tokens.getRefreshToken());
        return ResponseEntity.ok(
                new Response<>(HttpStatus.OK, body, "토큰이 전달되었습니다.")
        );
    }

    /** 리프레시 토큰으로 Access 토큰 재발급 */
    @PostMapping("/refresh")
    public ResponseEntity<Response<TokenResponse>> refreshToken(@RequestBody TokenResponse refreshRequest) {
        var tokens = authService.refreshAccessToken(refreshRequest.getRefresh_token());
        TokenResponse body = new TokenResponse(tokens.getUserId(), tokens.getAccessToken(), tokens.getRefreshToken());
        return ResponseEntity.ok(
                new Response<>(HttpStatus.OK, body, "리프레시 토큰이 재발급되었습니다.")
        );
    }
}
