package com.example.backendtaskmanagement.repositories;

import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND (t.title LIKE %:title% OR t.description LIKE %:description%)")
    List<Task> searchTasks(@Param("userId") Long userId, @Param("title") String title, @Param("description") String description);
}