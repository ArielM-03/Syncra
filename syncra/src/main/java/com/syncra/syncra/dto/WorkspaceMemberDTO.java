package com.syncra.syncra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.syncra.syncra.model.WorkspaceMember;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WorkspaceMemberDTO {
    private UserDTO user;
    private WorkspaceDTO workspace;
    private WorkspaceMember.MemberRole role;
    private LocalDateTime joinedAt;

    public static WorkspaceMemberDTO from(WorkspaceMember member) {
        return new WorkspaceMemberDTO(
                UserDTO.from(member.getUser()),
                WorkspaceDTO.from(member.getWorkspace()),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}