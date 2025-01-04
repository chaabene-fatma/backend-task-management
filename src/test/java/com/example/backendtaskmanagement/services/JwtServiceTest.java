package com.example.backendtaskmanagement.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", "mysecretkeymysecretkeymysecretkeymysecretkey");
        ReflectionTestUtils.setField(jwtService, "cookieExpiry", 3600);
    }

    @Test
    public void testGenerateToken_ShouldGenerateValidToken() {
        // Given
        String username = "testuser";

        // When
        String token = jwtService.GenerateToken(username);

        // Then
        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    public void testExtractUsername_ShouldExtractUsernameFromToken() {
        // Given
        String username = "testuser";
        String token = jwtService.GenerateToken(username);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testExtractExpiration_ShouldReturnFutureDate() {
        // Given
        String token = jwtService.GenerateToken("testuser");

        // When
        Date expirationDate = jwtService.extractExpiration(token);

        // Then
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    public void testValidateToken_ValidToken_ShouldReturnTrue() {
        // Given
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        String token = jwtService.GenerateToken(username);

        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_ExpiredToken_ShouldReturnFalse() {
        // Given
        ReflectionTestUtils.setField(jwtService, "cookieExpiry", 0);
        String token = jwtService.GenerateToken("testuser");
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());

        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void testValidateToken_InvalidUsername_ShouldReturnFalse() {
        // Given
        String token = jwtService.GenerateToken("testuser");
        UserDetails anotherUserDetails = new User("anotherUser", "password", new ArrayList<>());

        // When
        boolean isValid = jwtService.validateToken(token, anotherUserDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void testExtractClaim_ShouldReturnCorrectValue() {
        // Given
        String token = jwtService.GenerateToken("testuser");

        // When
        String claim = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals("testuser", claim);
    }
}
