package com.example.backendtaskmanagement.domain;

import java.time.LocalDate;

public record TaskDTO(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        String status,
        Long userId
) {}
