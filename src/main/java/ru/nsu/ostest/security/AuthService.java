package ru.nsu.ostest.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.apache.coyote.BadRequestException;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;

public interface AuthService {

    JwtResponse login(@NonNull UserPasswordDto authRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refresh(@NonNull String refreshToken);

    UserPasswordDto register(@NonNull UserCreationRequestDto userDto) throws BadRequestException;

    Long getUserIdFromJwt(HttpServletRequest request);
}