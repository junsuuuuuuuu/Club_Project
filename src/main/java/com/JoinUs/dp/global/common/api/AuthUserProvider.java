package com.JoinUs.dp.global.common.api;

import com.JoinUs.dp.common.exception.UnauthorizedException;
import com.JoinUs.dp.entity.User;
import com.JoinUs.dp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserProvider {

    private final UserRepository userRepository;

    public AuthUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return AuthUser.from(user);
        }

        // principal이 email 문자열인 경우 대응
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("사용자 정보를 찾을 수 없습니다."));
        return AuthUser.from(user);
    }
}
