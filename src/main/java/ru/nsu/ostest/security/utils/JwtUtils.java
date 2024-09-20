package ru.nsu.ostest.security.utils;

import io.jsonwebtoken.Claims;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.security.impl.JwtAuthentication;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtUtils {
    private JwtUtils() {
    }

    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setUserName(claims.get("username", String.class));
        return jwtInfoToken;
    }

    @SuppressWarnings("unchecked")
    private static Set<Role> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::new)
                .collect(Collectors.toSet());
    }
}