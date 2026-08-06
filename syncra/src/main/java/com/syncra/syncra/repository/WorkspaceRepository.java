package com.syncra.syncra.repository;

import com.syncra.syncra.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByCreatedBy_UserId(Long userId);
}