package com.JoinUs.dp.global.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    /**
     * application.yml 에서 읽어올 비밀키
     *
     * jwt:
     *   secret: "아주-길게-아무거나-적어놓은-시크릿키..."
     */
    @Value("${jwt.secret}")
    private String secretKey;

    // 30분
    private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30;
    // 7일
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;

    /** 내부에서 사용할 Key 객체 */
    private Key getSigningKey() {
        // HS256 에서는 32바이트 이상 권장 → 문자열을 바이트로 바꿔서 Key 생성
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /** Access Token 생성 */
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_VALIDITY);
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_VALIDITY);
    }

    /** 실제 토큰 생성 로직 */
    private String generateToken(String email, long validityMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 email(subject) 추출 */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료, 시그니처 오류 등등 → false
            return false;
        }
    }
}
