package com.JoinUs.dp.global.common.api;

import com.JoinUs.dp.entity.Role;
import com.JoinUs.dp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private Long id;
    private String email;
    private Role role;

    public static AuthUser from(User user) {
        return new AuthUser(user.getId(), user.getEmail(), user.getRole());
    }
}
