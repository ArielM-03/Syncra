package com.syncra.syncra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.syncra.syncra.model.Workspace;

@Getter
@AllArgsConstructor
public class WorkspaceDTO {
    private Long workspaceId;
    private String workspaceName;
    private UserDTO createdBy;

    public static WorkspaceDTO from(Workspace workspace) {
        return new WorkspaceDTO(
                workspace.getWorkspaceId(),
                workspace.getWorkspaceName(),
                UserDTO.from(workspace.getCreatedBy())
        );
    }
}