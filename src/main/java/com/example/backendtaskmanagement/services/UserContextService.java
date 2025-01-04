package com.example.backendtaskmanagement.services;

import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserContextService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser(String token) {
        String userEmail = jwtService.extractUsername(token);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userEmail));
    }
}
