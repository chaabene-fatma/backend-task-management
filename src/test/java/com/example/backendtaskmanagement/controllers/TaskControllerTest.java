package com.example.backendtaskmanagement.controllers;

import com.example.backendtaskmanagement.domain.*;
import com.example.backendtaskmanagement.exceptions.DuplicateTaskException;
import com.example.backendtaskmanagement.exceptions.ResourceNotFoundException;
import com.example.backendtaskmanagement.mappers.TaskMapper;
import com.example.backendtaskmanagement.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskController taskController;

    private Task task;
    private TaskDTO taskDTO;
    private User authenticatedUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticatedUser = new User();
        authenticatedUser.setId(1L);

        task = new Task();
        task.setId(1L);
        task.setTitle("PrediSurge task");

        taskDTO = new TaskDTO(1L,"Technical test", "Technical test", LocalDate.now(), TaskStatus.PENDING.toString(), 1L);
    }

    @Test
    public void testCreateTask_Success() {
        when(taskMapper.mapToEntity(taskDTO)).thenReturn(task);
        when(taskService.createTask(task, authenticatedUser)).thenReturn(task);
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.createTask(taskDTO, authenticatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDTO, response.getBody());
        verify(taskService, times(1)).createTask(task, authenticatedUser);
    }

    @Test
    public void testCreateTask_DuplicateTask() {
        when(taskMapper.mapToEntity(taskDTO)).thenReturn(task);
        when(taskService.createTask(task, authenticatedUser)).thenThrow(new DuplicateTaskException("Duplicate task"));

        assertThrows(DuplicateTaskException.class, () -> taskController.createTask(taskDTO, authenticatedUser));
    }

    @Test
    public void testUpdateTask_Success() {
        when(taskMapper.mapToEntity(taskDTO)).thenReturn(task);
        when(taskService.updateTask(1L, task)).thenReturn(Optional.of(task));
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, taskDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDTO, response.getBody());
        verify(taskService, times(1)).updateTask(1L, task);
    }

    @Test
    public void testUpdateTask_NotFound() {
        when(taskMapper.mapToEntity(taskDTO)).thenReturn(task);
        when(taskService.updateTask(1L, task)).thenReturn(Optional.empty());

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, taskDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetTaskById_Success() {
        when(taskService.getTaskById(1L, authenticatedUser)).thenReturn(task);
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<TaskDTO> response = taskController.getTaskById(1L, authenticatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    public void testDeleteTask_Success() {
        when(taskService.deleteTask(1L)).thenReturn(true);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteTask_NotFound() {
        doThrow(new ResourceNotFoundException("Task not found"))
                .when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllTasks() {
        when(taskService.getTasksForAuthenticatedUser(authenticatedUser)).thenReturn(List.of(task));
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(authenticatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testFilterTasks_Success() {
        when(taskService.filterTasksByStatus(authenticatedUser, TaskStatus.PENDING)).thenReturn(List.of(task));
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<List<TaskDTO>> response = taskController.filterTasks(TaskStatus.PENDING, authenticatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testSearchTasks() {
        when(taskService.searchTasks(authenticatedUser, "keyword")).thenReturn(List.of(task));
        when(taskMapper.mapToDTO(task)).thenReturn(taskDTO);

        ResponseEntity<List<TaskDTO>> response = taskController.searchTasks("keyword", authenticatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }
}
