package com.example.backendtaskmanagement.mappers;

import com.example.backendtaskmanagement.domain.Task;
import com.example.backendtaskmanagement.domain.TaskDTO;
import com.example.backendtaskmanagement.domain.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO mapToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus().name(),
                task.getUser().getId()
        );
    }

    public Task mapToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setId(taskDTO.id());
        task.setTitle(taskDTO.title());
        task.setDescription(taskDTO.description());
        task.setDueDate(taskDTO.dueDate());
        task.setStatus(TaskStatus.valueOf(taskDTO.status()));
        return task;
    }

}
