package ru.nsu.ostest.security;

import lombok.NonNull;
import org.apache.coyote.BadRequestException;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtRequest;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;

public interface AuthService {

    JwtResponse login(@NonNull JwtRequest authRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refresh(@NonNull String refreshToken);

    JwtRequest registration(@NonNull UserCreationRequestDto userDto) throws BadRequestException;
}