package com.example.backendtaskmanagement.services;

import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskStatus;
import com.example.backendtaskmanagement.domain.User;
import com.example.backendtaskmanagement.exceptions.InvalidTaskStatusException;
import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User authenticatedUser;
    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticatedUser = new User();
        authenticatedUser.setId(1L);

        task = new Task();
        task.setId(1L);
        task.setTitle("PrediSurge task");
        task.setDescription("Technical test");
        task.setDueDate(LocalDate.now());
        task.setStatus(TaskStatus.PENDING);
        task.setUser(authenticatedUser);
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(task)).thenReturn(task);
        Task result = taskService.createTask(task, authenticatedUser);
        assertNotNull(result);
        assertEquals(task.getUser(), result.getUser());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTask_Success() {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setDueDate(LocalDate.now().plusDays(1));
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Optional<Task> result = taskService.updateTask(1L, updatedTask);
        assertTrue(result.isPresent());
        assertEquals(updatedTask.getTitle(), result.get().getTitle());
        assertEquals(updatedTask.getStatus(), result.get().getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.updateTask(1L, task);
        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        boolean result = taskService.deleteTask(1L);
        assertTrue(result);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteTask_NotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
    }

    @Test
    public void testFilterTasksByStatus_Success() {
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.PENDING)).thenReturn(List.of(task));
        List<Task> result = taskService.filterTasksByStatus(authenticatedUser, TaskStatus.PENDING);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.PENDING, result.getFirst().getStatus());
    }

    @Test
    public void testFilterTasksByStatus_InvalidStatus() {
        assertThrows(InvalidTaskStatusException.class, () -> taskService.filterTasksByStatus(authenticatedUser, null));
    }

    @Test
    public void testSearchTasks() {
        when(taskRepository.searchTasks(1L, "keyword", "keyword")).thenReturn(List.of(task));
        List<Task> result = taskService.searchTasks(authenticatedUser, "keyword");
        assertEquals(1, result.size());
        assertEquals(task.getTitle(), result.getFirst().getTitle());
    }

    @Test
    public void testGetTasksForAuthenticatedUser() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));
        List<Task> result = taskService.getTasksForAuthenticatedUser(authenticatedUser);
        assertEquals(1, result.size());
        assertEquals(task.getUser().getId(), result.getFirst().getUser().getId());
    }

    @Test
    public void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Task result = taskService.getTaskById(1L, authenticatedUser);
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }

    @Test
    public void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L, authenticatedUser));
    }

    @Test
    public void testGetTaskById_NotAuthorized() {
        User otherUser = new User();
        otherUser.setId(2L);
        task.setUser(otherUser);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L, authenticatedUser));
    }
}
