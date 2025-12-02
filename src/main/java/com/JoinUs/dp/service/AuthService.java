package com.JoinUs.dp.service;

import com.JoinUs.dp.common.exception.ConflictException;
import com.JoinUs.dp.common.exception.UnauthorizedException;
import com.JoinUs.dp.dto.RegisterDto;
import com.JoinUs.dp.dto.TokenDto;
import com.JoinUs.dp.entity.User;
import com.JoinUs.dp.global.utility.JwtProvider;
import com.JoinUs.dp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /** 회원가입 */
    public void register(RegisterDto dto) {
        // 이메일/아이디 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("이미 사용 중인 이메일입니다.");
        }

        // 유저 엔티티 생성
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // NOT NULL 컬럼 기본값 처리
        String nickname = dto.getNickname() != null ? dto.getNickname() : dto.getUsername();
        String department = dto.getDepartment() != null ? dto.getDepartment() : "미정";
        String studentId = dto.getStudentId() != null ? dto.getStudentId() : "TEMP-" + System.currentTimeMillis();
        Integer grade = dto.getGrade() != null ? dto.getGrade() : 1;

        user.setNickname(nickname);
        user.setDepartment(department);
        user.setStudentId(studentId);
        user.setGrade(grade);

        userRepository.save(user);
    }

    /** 로그인 */
    public TokenDto login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return new TokenDto(accessToken, refreshToken);
    }

    /** 리프레시 토큰으로 액세스 토큰 재발급 */
    public TokenDto refreshAccessToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtProvider.extractEmail(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(email);
        String newRefreshToken = jwtProvider.generateRefreshToken(email);

        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
