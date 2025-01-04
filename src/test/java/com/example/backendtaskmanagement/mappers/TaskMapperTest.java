package com.example.backendtaskmanagement.mappers;

import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskDTO;
import com.example.backendtaskmanagement.domain.TaskStatus;
import com.example.backendtaskmanagement.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    public void setUp() {
        taskMapper = new TaskMapper();
    }

    @Test
    public void testMapToDTO() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("PrediSurge task");
        task.setDescription("Technical test");
        task.setDueDate(LocalDate.now());
        task.setStatus(TaskStatus.PENDING);
        User user = new User();
        user.setId(2L);
        task.setUser(user);

        TaskDTO taskDTO = taskMapper.mapToDTO(task);

        assertEquals(1L, taskDTO.id());
        assertEquals("PrediSurge task", taskDTO.title());
        assertEquals("Technical test", taskDTO.description());
        assertEquals(task.getDueDate(), taskDTO.dueDate());
        assertEquals("PENDING", taskDTO.status());
        assertEquals(2L, taskDTO.userId());
    }

    @Test
    public void testMapToEntity() {
        TaskDTO taskDTO = new TaskDTO(1L, "PrediSurge task", "Technical test", LocalDate.now(), "COMPLETED", 2L);

        Task task = taskMapper.mapToEntity(taskDTO);

        assertEquals(1L, task.getId());
        assertEquals("PrediSurge task", task.getTitle());
        assertEquals("Technical test", task.getDescription());
        assertEquals(taskDTO.dueDate(), task.getDueDate());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNull(task.getUser());
    }
}
