package ru.nsu.ostest.adapter.in.rest.model.user.auth;

import lombok.Data;

/**
 * DTO для ответа JWT при авторизации.
 */
@Data
public class JwtResponse {
    private String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}