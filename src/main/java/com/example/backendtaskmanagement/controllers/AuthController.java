package com.example.backendtaskmanagement.controllers;

import com.example.backendtaskmanagement.exceptions.UserAlreadyExistsException;
import com.example.backendtaskmanagement.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            userService.authenticateAndGenerateToken(email, password, response);
            responseMap.put("message", "Login successful");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            LOGGER.error("Login failed for user: {} - {}", email, e.getMessage());
            responseMap.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseMap);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        try {
            if (password == null || password.length() < 8) {
                response.put("error", "Password must be at least 8 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            userService.registerUser(email, password);
            LOGGER.info("User registered successfully: {}", email);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            LOGGER.error("Registration failed - user already exists: {}", email);
            response.put("error", "User already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            LOGGER.error("Registration failed for user: {} - {}", email, e.getMessage());
            response.put("error", "An error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        userService.clearCookie(response);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Logout successful");
        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String token = userService.extractTokenFromRequest(request);
        if (token != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}

