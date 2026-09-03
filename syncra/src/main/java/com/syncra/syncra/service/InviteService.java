package com.syncra.syncra.service;

import com.syncra.syncra.model.Invite;
import com.syncra.syncra.model.User;
import com.syncra.syncra.model.Workspace;
import com.syncra.syncra.model.WorkspaceMember;
import com.syncra.syncra.repository.InviteRepository;
import com.syncra.syncra.repository.UserRepository;
import com.syncra.syncra.repository.WorkspaceRepository;
import com.syncra.syncra.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public Invite createInvite(Long workspaceId, Long createdByUserId,
                               String email, WorkspaceMember.MemberRole role) {

        workspaceMemberService.requireRole(workspaceId, createdByUserId,
                WorkspaceMember.MemberRole.OWNER);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found."));

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (inviteRepository.existsByWorkspace_WorkspaceIdAndEmailAndAcceptedAtIsNull(
                workspaceId, email)) {
            throw new IllegalArgumentException("A pending invite already exists for this email.");
        }

        String token = generateToken();

        Invite invite = new Invite();
        invite.setWorkspace(workspace);
        invite.setEmail(email);
        invite.setRole(role);
        invite.setToken(token);
        invite.setExpiresAt(LocalDateTime.now().plusDays(7));
        invite.setCreatedBy(createdBy);

        return inviteRepository.save(invite);
    }

    public Invite acceptInvite(String token, Long userId) {
        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token."));

        if (invite.isExpired()) {
            throw new IllegalArgumentException("Invite token has expired.");
        }

        if (invite.isAccepted()) {
            throw new IllegalArgumentException("Invite has already been accepted.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        workspaceMemberService.addMember(
                invite.getWorkspace().getWorkspaceId(),
                userId,
                invite.getRole());

        invite.setAcceptedAt(LocalDateTime.now());
        return inviteRepository.save(invite);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}