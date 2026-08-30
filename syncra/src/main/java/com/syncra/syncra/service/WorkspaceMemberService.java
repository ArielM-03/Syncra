package com.syncra.syncra.service;

import com.syncra.syncra.model.User;
import com.syncra.syncra.model.Workspace;
import com.syncra.syncra.model.WorkspaceMember;
import com.syncra.syncra.repository.UserRepository;
import com.syncra.syncra.repository.WorkspaceMemberRepository;
import com.syncra.syncra.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceMemberService {

    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceMember addMember(Long workspaceId, Long userId,
                                     WorkspaceMember.MemberRole role) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (memberRepository.existsByWorkspace_WorkspaceIdAndUser_UserId(
                workspaceId, userId)) {
            throw new IllegalArgumentException("User is already a member.");
        }

        WorkspaceMember member = new WorkspaceMember();
        member.getId().setWorkspaceId(workspaceId);
        member.getId().setUserId(userId);
        member.setWorkspace(workspace);
        member.setUser(user);
        member.setRole(role);

        return memberRepository.save(member);
    }

    public WorkspaceMember.MemberRole getMemberRole(Long workspaceId, Long userId) {
        return memberRepository
                .findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                .map(WorkspaceMember::getRole)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User is not a member of this workspace."));
    }

    public void requireRole(Long workspaceId, Long userId,
                            WorkspaceMember.MemberRole minimumRole) {
        WorkspaceMember.MemberRole actual = getMemberRole(workspaceId, userId);

        boolean permitted = switch (minimumRole) {
            case VIEWER -> true;
            case MEMBER -> actual == WorkspaceMember.MemberRole.MEMBER
                    || actual == WorkspaceMember.MemberRole.OWNER;
            case OWNER -> actual == WorkspaceMember.MemberRole.OWNER;
        };

        if (!permitted) {
            throw new SecurityException("Insufficient permissions.");
        }
    }

    public List<WorkspaceMember> getMembersByWorkspace(Long workspaceId) {
        return memberRepository.findAll().stream()
                .filter(m -> m.getWorkspace().getWorkspaceId().equals(workspaceId))
                .toList();
    }
}