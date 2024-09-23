package ru.nsu.ostest.security.utils;

import io.jsonwebtoken.Claims;
import ru.nsu.ostest.security.impl.JwtAuthentication;

import java.util.List;

public class JwtUtils {
    private JwtUtils() {
    }

    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoleNames(claims.get("roles", List.class));
        jwtInfoToken.setUserName(claims.get("username", String.class));
        return jwtInfoToken;
    }
}