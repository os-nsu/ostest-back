package ru.nsu.ostest.security.impl;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ostest.adapter.in.rest.model.user.AccessAndRefreshTokens;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.service.UserService;
import ru.nsu.ostest.security.AuthService;
import ru.nsu.ostest.security.exceptions.AuthException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProviderImpl jwtProviderImpl;
    private final BlacklistService blacklistService;

    @Override
    public JwtResponse login(@NonNull UserPasswordDto authRequest) {
        log.info(AuthConstants.PROCESSING_LOGIN_REQUEST);
        User user = userService.findUserByUsername(authRequest.username());
        if (passwordEncoder.matches(authRequest.password(), user.getUserPassword().getPassword())) {
            return getJwtResponse(user);
        } else {
            log.error(AuthConstants.WRONG_PASSWORD_MESSAGE);
            throw new AuthException(AuthConstants.WRONG_PASSWORD_MESSAGE);
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
        log.info(AuthConstants.PROCESSING_GET_ACCESS_TOKEN_REQUEST);
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
        log.error(AuthConstants.VALIDATING_REFRESH_TOKEN_FAILED);
        throw new AuthException(AuthConstants.INVALID_JWT_MESSAGE);
    }

    @Override
    public JwtResponse refresh(@NonNull String refreshToken) {
        log.info(AuthConstants.PROCESSING_REFRESH_REQUEST);
        if (!jwtProviderImpl.validateRefreshToken(refreshToken)) {
            log.error(AuthConstants.VALIDATING_REFRESH_TOKEN_FAILED);
            throw new AuthException(AuthConstants.INVALID_JWT_MESSAGE);
        }
        final Claims claims = jwtProviderImpl.getRefreshClaims(refreshToken);
        final String userId = claims.getSubject();
        final String saveRefreshToken = refreshStorage.get(userId);
        if (saveRefreshToken == null || !saveRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthConstants.INVALID_JWT_MESSAGE);
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
    public void logout(@NonNull AccessAndRefreshTokens request) {
        log.info(AuthConstants.PROCESSING_LOGOUT_REQUEST);

        if (!jwtProviderImpl.validateRefreshToken(request.refreshToken())) {
            log.error(AuthConstants.VALIDATING_REFRESH_TOKEN_FAILED);
            throw new AuthException(AuthConstants.INVALID_JWT_MESSAGE);
        }

        Claims claims = jwtProviderImpl.getRefreshClaims(request.refreshToken());
        String userId = claims.getSubject();

        refreshStorage.remove(userId);
        blacklistService.addToBlacklist(request.accessToken());
    }
}