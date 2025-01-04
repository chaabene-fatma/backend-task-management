package com.example.backendtaskmanagement.services;

import com.example.backendtaskmanagement.domain.Role;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.exceptions.UserAlreadyExistsException;
import com.example.backendtaskmanagement.repositories.RoleRepository;
import com.example.backendtaskmanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_whenUserDoesNotExist_shouldSaveUser() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        Role userRole = new Role();
        userRole.setName("USER");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // When
        userService.registerUser(email, password);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void registerUser_whenUserAlreadyExists_shouldThrowException() {
        // Given
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(email, "password123"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateAndGenerateToken_shouldGenerateCookie() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String token = "generatedToken";

        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mock(org.springframework.security.core.userdetails.User.class));
        when(jwtService.GenerateToken(email)).thenReturn(token);

        // When
        userService.authenticateAndGenerateToken(email, password, response);

        // Then
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void clearCookie_shouldSetCookieWithMaxAgeZero() {
        // Given
        HttpServletResponse response = mock(HttpServletResponse.class);

        // When
        userService.clearCookie(response);

        // Then
        verify(response, times(1)).addCookie(argThat(cookie ->
                "auth_token".equals(cookie.getName()) &&
                        cookie.getMaxAge() == 0
        ));
    }

    @Test
    void extractTokenFromRequest_shouldReturnTokenFromCookie() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("auth_token", "cookieToken") };

        when(request.getCookies()).thenReturn(cookies);

        // When
        String extractedToken = userService.extractTokenFromRequest(request);

        // Then
        assertEquals("cookieToken", extractedToken);
    }

    @Test
    void extractTokenFromRequest_shouldReturnNullWhenNoTokenExists() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(null);

        // When
        String extractedToken = userService.extractTokenFromRequest(request);

        // Then
        assertNull(extractedToken);
    }
}
