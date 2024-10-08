package ru.nsu.ostest.security.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuthServiceCommon {
    public static JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean checkAuthorities(String login) {
        JwtAuthentication principal = getAuthInfo();
        if (principal == null || login == null) {
            return false;
        }
        return isContainsRole(principal.getAuthorities(), "ADMIN") || principal.getUserName().equals(login);
    }

    private static boolean isContainsRole(Collection<? extends GrantedAuthority> authorities, String name) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority() != null && authority.getAuthority().equals(name)) {
                return true;
            }
        }
        return false;
    }

}