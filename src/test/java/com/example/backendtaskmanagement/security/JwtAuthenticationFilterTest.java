package com.example.backendtaskmanagement.security;

import com.example.backendtaskmanagement.services.JwtService;
import com.example.backendtaskmanagement.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String token = "my-token";
    private final String username = "user@example.com";
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User(username, "password", Collections.emptyList());
    }

    @Test
    public void testDoFilterInternal_WithValidToken() throws Exception {
        // Given
        Cookie authCookie = new Cookie("auth_token", token);
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(token);
        verify(userDetailsServiceImpl).loadUserByUsername(username);
        verify(jwtService).validateToken(token, userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws Exception {
        // Given
        Cookie authCookie = new Cookie("auth_token", token);
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(token);
        verify(jwtService).validateToken(token, userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getCookies()).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsServiceImpl);
    }

    @Test
    public void testDoFilterInternal_ClearAuthCookieOnRegister() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/register");
        Cookie authCookie = new Cookie("auth_token", token);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).addCookie(argThat(cookie ->
                cookie.getName().equals("auth_token") &&
                        cookie.getMaxAge() == 0 &&
                        cookie.getValue() == null
        ));
        verify(filterChain).doFilter(request, response);
    }
}
