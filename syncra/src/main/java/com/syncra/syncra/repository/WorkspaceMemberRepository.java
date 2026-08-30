package com.syncra.syncra.repository;

import com.syncra.syncra.model.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository
        extends JpaRepository<WorkspaceMember, WorkspaceMember.WorkspaceMemberId> {

    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndUser_UserId(
            Long workspaceId, Long userId);

    boolean existsByWorkspace_WorkspaceIdAndUser_UserId(
            Long workspaceId, Long userId);
}