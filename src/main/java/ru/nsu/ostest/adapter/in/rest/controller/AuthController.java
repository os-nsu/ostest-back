package ru.nsu.ostest.adapter.in.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ostest.adapter.in.rest.model.user.auth.JwtResponse;
import ru.nsu.ostest.adapter.in.rest.model.user.auth.RefreshJwtRequest;
import ru.nsu.ostest.adapter.in.rest.model.user.password.UserPasswordDto;
import ru.nsu.ostest.adapter.in.rest.model.user.userData.LogoutRequest;
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

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
    }

}
