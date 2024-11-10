package ru.nsu.ostest.user;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import ru.nsu.ostest.adapter.in.rest.model.user.LogoutRequest;
import ru.nsu.ostest.security.exception.InvalidJwtException;
import ru.nsu.ostest.security.impl.AuthServiceImpl;
import ru.nsu.ostest.security.impl.BlacklistService;
import ru.nsu.ostest.security.impl.JwtProviderImpl;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private JwtProviderImpl jwtProviderImpl;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private ConcurrentHashMap<String, String> refreshStorage;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void logout_ShouldRemoveTokens_WhenValidRequest() {
        String validRefreshToken = "validRefreshToken";
        String validAccessToken = "validAccessToken";
        refreshStorage.put("userId", validRefreshToken);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.getSubject()).thenReturn("userId");

        when(jwtProviderImpl.validateRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtProviderImpl.getRefreshClaims(validRefreshToken)).thenReturn(claims);

        LogoutRequest request = new LogoutRequest(validAccessToken, validRefreshToken);

        authService.logout(request);
        assertFalse(refreshStorage.contains("userId"));
        verify(blacklistService, times(1)).addToBlacklist(validAccessToken);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void logout_ShouldNotRemoveTokens_WhenInvalidRequest() {
        String invalidRefreshToken = "invalidRefreshToken";
        String validAccessToken = "validAccessToken";

        when(jwtProviderImpl.validateRefreshToken(invalidRefreshToken)).thenReturn(false);

        LogoutRequest request = new LogoutRequest(validAccessToken, invalidRefreshToken);

        InvalidJwtException exception = assertThrows(InvalidJwtException.class, () -> authService.logout(request));

        verify(blacklistService, never()).addToBlacklist(anyString());
        verify(refreshStorage, never()).remove("userId");

        assertEquals("Invalid JWT", exception.getMessage());
    }
}