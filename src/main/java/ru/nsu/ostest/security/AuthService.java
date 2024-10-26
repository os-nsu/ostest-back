package ru.nsu.ostest.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import ru.nsu.ostest.adapter.in.rest.model.user.AccessAndRefreshTokens;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;

public interface AuthService {

    JwtResponse login(@NonNull UserPasswordDto authRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refresh(@NonNull String refreshToken);

    Long getUserIdFromJwt(HttpServletRequest request);

    void logout(@NonNull AccessAndRefreshTokens refreshToken);
}