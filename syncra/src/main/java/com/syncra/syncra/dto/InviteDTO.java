package com.syncra.syncra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.syncra.syncra.model.Invite;
import com.syncra.syncra.model.WorkspaceMember;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InviteDTO {
    private Long inviteId;
    private String email;
    private WorkspaceMember.MemberRole role;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
    private boolean expired;
    private boolean accepted;
    private UserDTO createdBy;
    private WorkspaceDTO workspace;

    public static InviteDTO from(Invite invite) {
        return new InviteDTO(
                invite.getInviteId(),
                invite.getEmail(),
                invite.getRole(),
                invite.getToken(),
                invite.getExpiresAt(),
                invite.getAcceptedAt(),
                invite.getCreatedAt(),
                invite.isExpired(),
                invite.isAccepted(),
                UserDTO.from(invite.getCreatedBy()),
                WorkspaceDTO.from(invite.getWorkspace())
        );
    }
}