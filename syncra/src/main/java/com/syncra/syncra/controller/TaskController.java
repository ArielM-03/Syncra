package com.syncra.syncra.controller;

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
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> request) {
        Long workspaceId = Long.valueOf(request.get("workspaceId").toString());
        Long createdByUserId = Long.valueOf(request.get("createdByUserId").toString());
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        Long assignedToUserId = request.get("assignedToUserId") != null
                ? Long.valueOf(request.get("assignedToUserId").toString())
                : null;

        return ResponseEntity.ok(taskService.createTask(workspaceId, createdByUserId, title, description, assignedToUserId));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<Task>> getTasksByWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(taskService.getTasksByWorkspace(workspaceId));
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId));
    }

    @GetMapping("/workspace/{workspaceId}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(
            @PathVariable Long workspaceId,
            @PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByWorkspaceAndStatus(workspaceId, status));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTasksById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        Task.TaskStatus newStatus = Task.TaskStatus.valueOf(request.get("status"));
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, newStatus));
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<Task> assignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(taskService.assignTask(taskId, userId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
