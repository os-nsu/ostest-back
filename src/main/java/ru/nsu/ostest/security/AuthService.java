package ru.nsu.ostest.security;

import lombok.NonNull;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;

public interface AuthService {

    JwtResponse login(@NonNull UserPasswordDto authRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refresh(@NonNull String refreshToken);
}