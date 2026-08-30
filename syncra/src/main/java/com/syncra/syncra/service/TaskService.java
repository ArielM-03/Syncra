package com.syncra.syncra.service;

import com.syncra.syncra.model.Task;
import com.syncra.syncra.model.User;
import com.syncra.syncra.model.Workspace;
import com.syncra.syncra.repository.TaskRepository;
import com.syncra.syncra.repository.UserRepository;
import com.syncra.syncra.repository.WorkspaceRepository;
import com.syncra.syncra.model.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService workspaceMemberService;

    public Task createTask(Long workspaceId, Long createdByUserId,
                            String title, String description, Long assignedToUserId) {

        workspaceMemberService.requireRole(workspaceId, createdByUserId,
                WorkspaceMember.MemberRole.MEMBER);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found."));

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Task task = new Task();
        task.setWorkspace(workspace);
        task.setCreatedBy(createdBy);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(Task.TaskStatus.TODO);

        if (assignedToUserId != null) {
            User assignedTo = userRepository.findById(assignedToUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found."));
            task.setAssignedTo(assignedTo);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public List<Task> getTasksByWorkspace(Long workspaceId) {
        return taskRepository.findByWorkspace_WorkspaceId(workspaceId);
    }

    @Transactional
    public List<Task> getTasksByAssignee(Long userId) {
        return taskRepository.findByAssignedTo_UserId(userId);
    }

    @Transactional
    public List<Task> getTasksByWorkspaceAndStatus(Long workspaceId, Task.TaskStatus status) {
        return taskRepository.findByWorkspace_WorkspaceIdAndStatus(workspaceId, status);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task updateTaskStatus(Long taskId, Task.TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found."));

        workspaceMemberService.requireRole(
                task.getWorkspace().getWorkspaceId(),
                task.getCreatedBy().getUserId(),
                WorkspaceMember.MemberRole.MEMBER);

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public Task assignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        task.setAssignedTo(user);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

}
