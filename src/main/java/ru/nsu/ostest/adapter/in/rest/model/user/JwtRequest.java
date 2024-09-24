package ru.nsu.ostest.adapter.in.rest.model.user;

public record JwtRequest(
        String username,
        String password) {
}