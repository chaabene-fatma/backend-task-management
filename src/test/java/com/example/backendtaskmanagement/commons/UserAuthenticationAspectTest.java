package com.example.backendtaskmanagement.commons;

import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.exceptions.UnauthorizedException;
import com.example.backendtaskmanagement.services.UserContextService;
import com.example.backendtaskmanagement.services.UserService;
import jakarta.servlet.http.Cookie;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class UserAuthenticationAspectTest {

    @Mock
    private UserContextService userContextService;

    @Mock
    private UserService userService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private UserAuthenticationAspect userAuthenticationAspect;

    @Test
    public void testAuthenticateAndProceed_Success() throws Throwable {
        // Given
        String token = "valid-token";
        User authenticatedUser = new User();
        authenticatedUser.setId(1L); // Set up a dummy user

        // Simulate the request context with a MockHttpServletRequest
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setCookies(new Cookie("auth_token", token));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // Mock behaviors
        when(userService.extractTokenFromRequest(mockRequest)).thenReturn(token);
        when(userContextService.getAuthenticatedUser(token)).thenReturn(authenticatedUser);
        when(joinPoint.getArgs()).thenReturn(new Object[]{authenticatedUser});
        when(joinPoint.proceed(any(Object[].class))).thenReturn("Success");

        // When
        Object result = userAuthenticationAspect.authenticateAndProceed(joinPoint);

        // Then
        verify(joinPoint).proceed(any(Object[].class));
        assertEquals("Success", result);
    }


    @Test
    public void testAuthenticateAndProceed_Unauthorized() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // Prepare the test data
        String token = null;

        // Mock behaviors
        when(userService.extractTokenFromRequest(mockRequest)).thenReturn(token);

        // When
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userAuthenticationAspect.authenticateAndProceed(joinPoint));

        // Then
        assertEquals("Auth token not found in request", exception.getMessage());
    }
}
