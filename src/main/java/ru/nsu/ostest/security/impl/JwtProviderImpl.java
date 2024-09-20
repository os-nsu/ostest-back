package ru.nsu.ostest.security.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.nsu.ostest.adapter.out.persistence.entity.user.Role;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.adapter.out.persistence.entity.user.UserRole;
import ru.nsu.ostest.security.JwtProvider;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProviderImpl(@Value("${jwt.secret.access}") String jwtAccessSecret,
                           @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
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
        log.info("Generated access token for user  with id [{}]", user.getId());
        return Jwts.builder().setSubject(String.valueOf(user.getId())).setExpiration(accessExpiration).signWith(jwtAccessSecret).claim(
                "roles", roles).claim("group", user.getGroup().getId()).claim("username", user.getUsername()).compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        log.info("Generating refresh token for user with id [{}]", user.getId());
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        log.info("Generated refresh token for user with id [{}]", user.getId());
        return Jwts.builder().setSubject(String.valueOf(user.getId())).setExpiration(refreshExpiration).signWith(
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
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
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
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

}