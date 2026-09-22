package com.syncra.syncra.controller;

import com.syncra.syncra.dto.WorkspaceDTO;
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
    public ResponseEntity<WorkspaceDTO> createWorkspace(
            @RequestBody Map<String, Object> request) {
        String workspaceName = (String) request.get("workspaceName");
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(
                WorkspaceDTO.from(
                        workspaceService.createWorkspace(workspaceName, userId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkspaceDTO>> getWorkspacesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                workspaceService.getWorkspacesByUser(userId).stream()
                        .map(WorkspaceDTO::from)
                        .toList());
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> getWorkspaceById(
            @PathVariable Long workspaceId) {
        return workspaceService.getWorkspaceById(workspaceId)
                .map(WorkspaceDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(
            @PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }
}