package ru.nsu.ostest.adapter.in.rest.model.user.password;

public record ChangePasswordDto(String oldPassword, String newPassword) {
}
