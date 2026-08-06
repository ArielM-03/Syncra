package com.syncra.syncra.controller;

import com.syncra.syncra.model.Workspace;
import com.syncra.syncra.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<Workspace> createWorkspace(@RequestBody Map<String, Object> request) {
        String workspaceName = (String) request.get("workspaceName");
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(workspaceService.createWorkspace(workspaceName, userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Workspace>> getWorkspacesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(workspaceService.getWorkspacesByUser(userId));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable Long workspaceId) {
        return workspaceService.getWorkspaceById(workspaceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }
}