package ru.nsu.ostest.adapter.in.rest.model.user.userData;

public record LogoutRequest(String accessToken, String refreshToken) {
}
