package com.syncra.syncra.controller;

import com.syncra.syncra.model.Invite;
import com.syncra.syncra.model.WorkspaceMember;
import com.syncra.syncra.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/invites")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @PostMapping
    public ResponseEntity<Invite> createInvite(
            @PathVariable Long workspaceId,
            @RequestBody Map<String, Object> request) {
        Long createdByUserId = Long.valueOf(request.get("createdByUserId").toString());
        String email = (String) request.get("email");
        WorkspaceMember.MemberRole role = WorkspaceMember.MemberRole.valueOf(
                request.get("role").toString());
        return ResponseEntity.ok(
                inviteService.createInvite(workspaceId, createdByUserId, email, role));
    }

    @PostMapping("/accept")
    public ResponseEntity<Invite> acceptInvite(
            @PathVariable Long workspaceId,
            @RequestBody Map<String, Object> request) {
        String token = (String) request.get("token");
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(inviteService.acceptInvite(token, userId));
    }
}