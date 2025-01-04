package com.example.backendtaskmanagement.services;

import com.example.backendtaskmanagement.commons.RequiresAuthentication;
import com.example.backendtaskmanagement.exceptions.InvalidTaskStatusException;
import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskStatus;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiresAuthentication
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @RequiresAuthentication
    public Task createTask(Task task, User authenticatedUser) {
        task.setUser(authenticatedUser);
        return taskRepository.save(task);
    }

    @RequiresAuthentication
    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDueDate(updatedTask.getDueDate());
            task.setStatus(updatedTask.getStatus());
            return taskRepository.save(task);
        });
    }

    @RequiresAuthentication
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
        return true;
    }

    @RequiresAuthentication
    public List<Task> filterTasksByStatus(User authenticatedUser, TaskStatus status) {
        if (status == null) {
            throw new InvalidTaskStatusException("Invalid task status provided");
        }
        return taskRepository.findByUserIdAndStatus(authenticatedUser.getId(), status);
    }

    @RequiresAuthentication
    public List<Task> searchTasks(User authenticatedUser, String keyword) {
        return taskRepository.searchTasks(authenticatedUser.getId(), keyword, keyword);
    }

    @RequiresAuthentication
    public List<Task> getTasksForAuthenticatedUser(User authenticatedUser) {
        return taskRepository.findByUserId(authenticatedUser.getId());
    }

    @RequiresAuthentication
    public Task getTaskById(Long taskId, User authenticatedUser) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(authenticatedUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found or not accessible"));
    }

}