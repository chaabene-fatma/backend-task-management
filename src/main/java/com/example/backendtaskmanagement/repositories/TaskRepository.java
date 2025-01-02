package com.example.backendtaskmanagement.repositories;

import com.example.backendtaskmanagement.models.Task;
import com.example.backendtaskmanagement.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    List<Task> findByUserIdAndTitleContainingOrDescriptionContaining(Long userId, String title, String description);
}