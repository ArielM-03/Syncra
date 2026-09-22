package com.syncra.syncra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.syncra.syncra.model.Task;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskDTO {
    private Long taskId;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private LocalDateTime dueAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdBy;
    private UserDTO assignedTo;
    private WorkspaceDTO workspace;

    public static TaskDTO from(Task task) {
        return new TaskDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueAt(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                UserDTO.from(task.getCreatedBy()),
                task.getAssignedTo() != null
                        ? UserDTO.from(task.getAssignedTo())
                        : null,
                WorkspaceDTO.from(task.getWorkspace())
        );
    }
}