package com.example.backendtaskmanagement.controllers;

import com.example.backendtaskmanagement.exceptions.UserAlreadyExistsException;
import com.example.backendtaskmanagement.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Given
        String email = "user@example.com";
        String password = "validPassword";
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Login successful");

        // When
        doNothing().when(userService).authenticateAndGenerateToken(eq(email), eq(password), any(HttpServletResponse.class));

        // Then
        mockMvc.perform(post("/auth/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService, times(1)).authenticateAndGenerateToken(eq(email), eq(password), any(HttpServletResponse.class));
    }

    @Test
    public void testLogin_Failure() throws Exception {
        // Given
        String email = "user@example.com";
        String password = "invalidPassword";

        // When
        doThrow(new RuntimeException("Invalid credentials"))
                .when(userService).authenticateAndGenerateToken(eq(email), eq(password), any());

        // Then
        mockMvc.perform(post("/auth/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }


    @Test
    public void testRegister_Success() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "validPassword";

        // When
        doNothing().when(userService).registerUser(email, password);

        // Then
        mockMvc.perform(post("/auth/register")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    public void testRegister_UserAlreadyExists() throws Exception {
        // Given
        String email = "existinguser@example.com";
        String password = "validPassword";

        // When
        doThrow(new UserAlreadyExistsException("User already exists")).when(userService).registerUser(email, password);

        // Then
        mockMvc.perform(post("/auth/register")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists"));
    }

    @Test
    public void testRegister_PasswordTooShort() throws Exception {
        // Given
        String email = "user@example.com";
        String password = "short";

        // Then
        mockMvc.perform(post("/auth/register")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password must be at least 8 characters long"));
    }

    @Test
    public void testLogout() throws Exception {
        doNothing().when(userService).clearCookie(any(HttpServletResponse.class));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));

        verify(userService, times(1)).clearCookie(any(HttpServletResponse.class));
    }

    @Test
    public void testVerifyToken_Success() throws Exception {
        // Given
        String token = "valid-token";

        // When
        when(userService.extractTokenFromRequest(any())).thenReturn(token);

        // Then
        mockMvc.perform(get("/auth/verify"))
                .andExpect(status().isOk());
    }

    @Test
    public void testVerifyToken_Unauthorized() throws Exception {
        // Given
        String token = null;

        // When
        when(userService.extractTokenFromRequest(any())).thenReturn(token);

        // Then
        mockMvc.perform(get("/auth/verify"))
                .andExpect(status().isUnauthorized());
    }
}
