package com.syncra.syncra.service;

import com.syncra.syncra.model.User;
import com.syncra.syncra.model.Workspace;
import com.syncra.syncra.model.WorkspaceMember;
import com.syncra.syncra.repository.UserRepository;
import com.syncra.syncra.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberService workspaceMemberService;

    @Transactional
    public Workspace createWorkspace(String workspaceName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Workspace workspace = new Workspace();
        workspace.setWorkspaceName(workspaceName);
        workspace.setCreatedBy(user);

        Workspace saved = workspaceRepository.save(workspace);

        workspaceMemberService.addMember(
                saved.getWorkspaceId(), userId, WorkspaceMember.MemberRole.OWNER);

        return saved;
    }

    @Transactional
    public List<Workspace> getWorkspacesByUser(Long userId) {
        return workspaceRepository.findByCreatedBy_UserId(userId);
    }

    @Transactional
    public Optional<Workspace> getWorkspaceById(Long workspaceId) {
        return workspaceRepository.findById(workspaceId);
    }

    public void deleteWorkspace(Long workspaceId) {
        workspaceRepository.deleteById(workspaceId);
    }
}