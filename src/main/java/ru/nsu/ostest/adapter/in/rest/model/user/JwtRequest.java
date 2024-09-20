package ru.nsu.ostest.adapter.in.rest.model.user;

/**
 * DTO для запроса JWT при авторизации.
 */
public record JwtRequest(
        String username,
        String password) {
}