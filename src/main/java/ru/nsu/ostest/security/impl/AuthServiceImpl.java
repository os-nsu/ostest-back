package ru.nsu.ostest.security.impl;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.auth.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.LogoutRequest;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.service.UserService;
import ru.nsu.ostest.security.AuthService;
import ru.nsu.ostest.security.exception.InvalidJwtException;
import ru.nsu.ostest.security.exception.WrongPasswordException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final Map<String, String> refreshStorage = new ConcurrentHashMap<>();
    private final JwtProviderImpl jwtProviderImpl;
    private final BlacklistService blacklistService;

    public static final String VALIDATING_REFRESH_TOKEN_FAILED_MESSAGE = "Validating refresh token failed";

    @Override
    public JwtResponse login(@NonNull UserPasswordDto authRequest) {
        log.info("Processing login request");
        User user = userService.findUserByUsername(authRequest.username());
        if (passwordEncoder.matches(authRequest.password(), user.getUserPassword().getPassword())) {
            return getJwtResponse(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    private JwtResponse getJwtResponse(User user) {
        final String accessToken = jwtProviderImpl.generateAccessToken(user);
        final String refreshToken = jwtProviderImpl.generateRefreshToken(user);
        refreshStorage.put(String.valueOf(user.getId()), refreshToken);
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setRefreshToken(refreshToken);
        return jwtResponse;
    }

    @Override
    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        log.info("Processing get access token request");
        if (jwtProviderImpl.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProviderImpl.getRefreshClaims(refreshToken);
            final String userId = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(userId);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findUserById(Long.valueOf(userId));
                final String accessToken = jwtProviderImpl.generateAccessToken(user);
                JwtResponse jwtResponse = new JwtResponse();
                jwtResponse.setAccessToken(accessToken);
                return jwtResponse;
            }
        }
        log.error(VALIDATING_REFRESH_TOKEN_FAILED_MESSAGE);
        throw new InvalidJwtException();
    }

    @Override
    public JwtResponse refresh(@NonNull String refreshToken) {
        log.info("Processing refresh request");
        if (!jwtProviderImpl.validateRefreshToken(refreshToken)) {
            log.error(VALIDATING_REFRESH_TOKEN_FAILED_MESSAGE);
            throw new InvalidJwtException();
        }
        final Claims claims = jwtProviderImpl.getRefreshClaims(refreshToken);
        final String userId = claims.getSubject();
        final String saveRefreshToken = refreshStorage.get(userId);
        if (saveRefreshToken == null || !saveRefreshToken.equals(refreshToken)) {
            throw new InvalidJwtException();
        }
        User user = userService.findUserById(Long.valueOf(userId));
        return getJwtResponse(user);
    }


    public Long getUserIdFromJwt(HttpServletRequest request) {
        String jwt = jwtProviderImpl.getTokenFromRequest(request);
        if (jwt != null && jwtProviderImpl.validateAccessToken(jwt)) {
            return jwtProviderImpl.getUserIdFromJwt(jwt);
        }
        return null;
    }

    @Override
    public void logout(@NonNull LogoutRequest request) {
        log.info("Processing logout request");

        if (!jwtProviderImpl.validateRefreshToken(request.refreshToken())) {
            log.error(VALIDATING_REFRESH_TOKEN_FAILED_MESSAGE);
            throw new InvalidJwtException();
        }

        Claims claims = jwtProviderImpl.getRefreshClaims(request.refreshToken());
        String userId = claims.getSubject();

        refreshStorage.remove(userId);
        blacklistService.addToBlacklist(request.accessToken());
    }
}