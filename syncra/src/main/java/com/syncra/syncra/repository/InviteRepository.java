package com.syncra.syncra.repository;

import com.syncra.syncra.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByToken(String token);
    boolean existsByWorkspace_WorkspaceIdAndEmailAndAcceptedAtIsNull(
            Long workspaceId, String email);
}