package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ostest.adapter.in.rest.model.user.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.RefreshJwtRequest;
import ru.nsu.ostest.adapter.in.rest.model.user.UserCreationRequestDto;
import ru.nsu.ostest.adapter.in.rest.model.user.UserPasswordDto;
import ru.nsu.ostest.security.AuthService;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody UserPasswordDto authRequest) {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPasswordDto register(@RequestBody UserCreationRequestDto user) throws BadRequestException {
        return authService.register(user);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<JwtResponse> getNewRefresh(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/auth/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

}
