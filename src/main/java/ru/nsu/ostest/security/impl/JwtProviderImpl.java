package ru.nsu.ostest.security.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserRole;
import ru.nsu.ostest.security.JwtProvider;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final BlacklistService blacklistService;

    public JwtProviderImpl(@Value("${jwt.secret.access}") String jwtAccessSecret,
                           @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
                           BlacklistService blacklistService) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.blacklistService = blacklistService;
    }

    public String generateAccessToken(@NonNull User user) {
        log.info("Generating access token for user with id [{}]", user.getId());
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        final List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles().stream().map(UserRole::getRole).toList()) {
            roles.add(role.getName());
        }
        List<String> groups = Optional.of(user)
                .map(User::getGroups)
                .stream().flatMap(Collection::stream)
                .map(Group::getName)
                .toList();
        log.info("Generated access token for user  with id [{}]", user.getId());
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(accessExpiration).signWith(jwtAccessSecret)
                .claim("roles", roles)
                .claim("groups", groups)
                .claim("username", user.getUsername()).compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        log.info("Generating refresh token for user with id [{}]", user.getId());
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        log.info("Generated refresh token for user with id [{}]", user.getId());
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(refreshExpiration).signWith(
                        jwtRefreshSecret).compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        log.info("Validating access token");
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        log.info("Validating refresh token");
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        if (blacklistService.isTokenBlacklisted(token)) {
            log.error("Token is blacklisted");
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired exception");
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt exception");
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt exception");
        } catch (Exception e) {
            log.error("invalid token exception");
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public Long getUserIdFromJwt(String token) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(jwtAccessSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

}