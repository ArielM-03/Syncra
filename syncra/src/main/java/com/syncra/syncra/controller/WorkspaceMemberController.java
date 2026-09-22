package com.syncra.syncra.controller;

import com.syncra.syncra.dto.WorkspaceMemberDTO;
import com.syncra.syncra.model.WorkspaceMember;
import com.syncra.syncra.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
@RequiredArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @PostMapping
    public ResponseEntity<WorkspaceMemberDTO> addMember(
            @PathVariable Long workspaceId,
            @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        WorkspaceMember.MemberRole role = WorkspaceMember.MemberRole.valueOf(
                request.get("role").toString());
        return ResponseEntity.ok(
                WorkspaceMemberDTO.from(
                        workspaceMemberService.addMember(workspaceId, userId, role)));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceMemberDTO>> getMembers(
            @PathVariable Long workspaceId) {
        return ResponseEntity.ok(
                workspaceMemberService.getMembersByWorkspace(workspaceId).stream()
                        .map(WorkspaceMemberDTO::from)
                        .toList());
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<WorkspaceMember.MemberRole> getMemberRole(
            @PathVariable Long workspaceId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                workspaceMemberService.getMemberRole(workspaceId, userId));
    }
}