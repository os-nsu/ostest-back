package ru.nsu.ostest.security.impl;

public final class AuthConstants {
    public static final String WRONG_PASSWORD_MESSAGE = "Wrong password";
    public static final String INVALID_JWT_MESSAGE = "Invalid JWT";
    public static final String PROCESSING_LOGIN_REQUEST = "Processing login request";
    public static final String PROCESSING_GET_ACCESS_TOKEN_REQUEST = "Processing get access token request";
    public static final String PROCESSING_REFRESH_REQUEST = "Processing refresh request";
    public static final String VALIDATING_REFRESH_TOKEN_FAILED = "Validating refresh token failed";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid token: ";

    private AuthConstants() {
    }
}