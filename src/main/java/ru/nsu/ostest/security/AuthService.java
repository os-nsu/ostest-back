package ru.nsu.ostest.security;

import lombok.NonNull;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtRequest;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;

public interface AuthService {

    JwtResponse login(@NonNull JwtRequest authRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refresh(@NonNull String refreshToken);

}