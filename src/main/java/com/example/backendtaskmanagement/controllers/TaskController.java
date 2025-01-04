package com.example.backendtaskmanagement.controllers;

import com.example.backendtaskmanagement.commons.RequiresAuthentication;
import com.example.backendtaskmanagement.exceptions.DuplicateTaskException;
import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.mappers.TaskMapper;
import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskDTO;
import com.example.backendtaskmanagement.domain.TaskStatus;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiresAuthentication
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO, @RequiresAuthentication User authenticatedUser) {
        LOGGER.info("Creating task with title: {}", taskDTO.title());
        try {
            Task task = taskService.createTask(taskMapper.mapToEntity(taskDTO), authenticatedUser);
            LOGGER.info("Task created successfully with ID: {}", task.getId());
            return ResponseEntity.ok(taskMapper.mapToDTO(task));
        } catch (DuplicateTaskException ex) {
            throw new DuplicateTaskException("A task with this title already exists");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        LOGGER.info("Update task with ID: {}", id);
        return taskService.updateTask(id, taskMapper.mapToEntity(taskDTO))
                .map(updatedTask -> {
                    LOGGER.info("Task updated successfully with ID: {}", updatedTask.getId());
                    return ResponseEntity.ok(taskMapper.mapToDTO(updatedTask));
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id, @RequiresAuthentication User authenticatedUser) {
        Task task = taskService.getTaskById(id, authenticatedUser);
        LOGGER.info("Fetch Task successfully with ID: {}", id);
        return ResponseEntity.ok(taskMapper.mapToDTO(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        LOGGER.info("Deleting task with ID: {}", id);
        try {
            if (taskService.deleteTask(id)) {
                LOGGER.info("Task deleted successfully with ID: {}", id);
                return ResponseEntity.noContent().build();
            }
        } catch (ResourceNotFoundException ex) {
            LOGGER.error("Task with ID: {} not found for deletion", id, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        LOGGER.warn("Task with ID: {} could not be deleted", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(@RequiresAuthentication User authenticatedUser) {
        List<Task> tasks = taskService.getTasksForAuthenticatedUser(authenticatedUser);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(taskMapper::mapToDTO)
                .collect(Collectors.toList());
        LOGGER.info("Retrieved {} tasks", taskDTOs.size());
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TaskDTO>> filterTasks(@RequestParam TaskStatus status, @RequiresAuthentication User authenticatedUser) {
        LOGGER.info("Filtering tasks with status: {}", status);
        List<Task> tasks = taskService.filterTasksByStatus(authenticatedUser, status);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(taskMapper::mapToDTO)
                .collect(Collectors.toList());
        LOGGER.info("Filtered {} tasks with status: {}", tasks.size(), status);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String keyword, @RequiresAuthentication User authenticatedUser) {
        LOGGER.info("Searching tasks with keyword: {}", keyword);
        List<Task> tasks = taskService.searchTasks(authenticatedUser, keyword);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(taskMapper::mapToDTO)
                .collect(Collectors.toList());
        LOGGER.info("Found {} tasks with keyword: {}", tasks.size(), keyword);
        return ResponseEntity.ok(taskDTOs);
    }

}
