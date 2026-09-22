package com.syncra.syncra.controller;

import com.syncra.syncra.dto.TaskDTO;
import com.syncra.syncra.model.Task;
import com.syncra.syncra.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody Map<String, Object> request) {
        Long workspaceId = Long.valueOf(request.get("workspaceId").toString());
        Long createdByUserId = Long.valueOf(request.get("createdByUserId").toString());
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        Long assignedToUserId = request.get("assignedToUserId") != null
                ? Long.valueOf(request.get("assignedToUserId").toString())
                : null;
        return ResponseEntity.ok(
                TaskDTO.from(taskService.createTask(
                        workspaceId, createdByUserId, title, description, assignedToUserId)));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<TaskDTO>> getTasksByWorkspace(
            @PathVariable Long workspaceId) {
        return ResponseEntity.ok(
                taskService.getTasksByWorkspace(workspaceId).stream()
                        .map(TaskDTO::from)
                        .toList());
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                taskService.getTasksByAssignee(userId).stream()
                        .map(TaskDTO::from)
                        .toList());
    }

    @GetMapping("/workspace/{workspaceId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @PathVariable Long workspaceId,
            @PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(
                taskService.getTasksByWorkspaceAndStatus(workspaceId, status).stream()
                        .map(TaskDTO::from)
                        .toList());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId)
                .map(TaskDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        Task.TaskStatus newStatus = Task.TaskStatus.valueOf(request.get("status"));
        return ResponseEntity.ok(
                TaskDTO.from(taskService.updateTaskStatus(taskId, newStatus)));
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(
                TaskDTO.from(taskService.assignTask(taskId, userId)));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}