package com.example.backendtaskmanagement.services;

import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserContextServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserContextService userContextService;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample user and token
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        token = "my-token";
    }

    @Test
    void testGetAuthenticatedUser_Success() {
        // Given
        when(jwtService.extractUsername(token)).thenReturn(user.getEmail());

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));

        // Then
        User authenticatedUser = userContextService.getAuthenticatedUser(token);
        assertNotNull(authenticatedUser);
        assertEquals(user.getEmail(), authenticatedUser.getEmail());
    }

    @Test
    void testGetAuthenticatedUser_UserNotFound() {
        // Given
        when(jwtService.extractUsername(token)).thenReturn(user.getEmail());

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.empty());

        // Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userContextService.getAuthenticatedUser(token));
        assertEquals("User not found for email: " + user.getEmail(), exception.getMessage());
    }

    @Test
    void testGetAuthenticatedUser_JwtServiceException() {
        // Given
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // When
        // Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userContextService.getAuthenticatedUser(token));
        assertEquals("Invalid token", exception.getMessage());
    }
}
