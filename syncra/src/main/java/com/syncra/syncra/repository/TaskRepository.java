package com.syncra.syncra.repository;

import com.syncra.syncra.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByWorkspace_WorkspaceId(Long workspaceId);
    List<Task> findByAssignedTo_UserId(Long userId);
    List<Task> findByWorkspace_WorkspaceIdAndStatus(Long workspaceId, Task.TaskStatus status);
}