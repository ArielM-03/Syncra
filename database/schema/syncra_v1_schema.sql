-- Syncra Database Schema (V1)
-- Author: Ariel I. Martinez
-- Description:
--   Initial relational schema for the Syncra team collaboration platform.
--   Includes users, workspaces, tasks, comments, activity logs, and invitations.
-- Database: MySQL 8+
-- Created: Jan 2026


CREATE DATABASE IF NOT EXISTS Syncra
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE Syncra;

-- Drop in dependency order (children -> parents)
DROP TABLE IF EXISTS task_activity;
DROP TABLE IF EXISTS task_comments;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS invites;
DROP TABLE IF EXISTS workspace_members;
DROP TABLE IF EXISTS workspaces;
DROP TABLE IF EXISTS users;

-- User Table
CREATE TABLE users (
  user_id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_email     VARCHAR(254) NOT NULL,
  password_hash  VARCHAR(255) NOT NULL,
  display_name   VARCHAR(100) NOT NULL,
  created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (user_id),
  UNIQUE KEY uq_users_email (user_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Workspaces Table
CREATE TABLE workspaces (
  workspace_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  workspace_name VARCHAR(120) NOT NULL,
  created_by     BIGINT UNSIGNED NOT NULL,
  created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (workspace_id),
  KEY idx_workspaces_created_by (created_by),
  CONSTRAINT fk_workspaces_created_by
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Members of workspace Table (many-to-many + role)
CREATE TABLE workspace_members (
  workspace_id BIGINT UNSIGNED NOT NULL,
  user_id      BIGINT UNSIGNED NOT NULL,
  role         ENUM('OWNER','MEMBER','VIEWER') NOT NULL DEFAULT 'MEMBER',
  joined_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (workspace_id, user_id),
  KEY idx_wm_user (user_id),
  CONSTRAINT fk_wm_workspace
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_wm_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Invite Table
CREATE TABLE invites (
  invite_id     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  workspace_id  BIGINT UNSIGNED NOT NULL,
  email         VARCHAR(254) NOT NULL,
  role          ENUM('MEMBER','VIEWER') NOT NULL DEFAULT 'MEMBER',
  token         CHAR(64) NOT NULL,
  expires_at    DATETIME(3) NOT NULL,
  accepted_at   DATETIME(3) NULL,
  created_by    BIGINT UNSIGNED NOT NULL,
  created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (invite_id),
  UNIQUE KEY uq_invites_token (token),
  KEY idx_invites_workspace (workspace_id),
  KEY idx_invites_email (email),
  KEY idx_invites_created_by (created_by),
  CONSTRAINT fk_invites_workspace
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_invites_created_by
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Task Table
CREATE TABLE tasks (
  task_id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  workspace_id  BIGINT UNSIGNED NOT NULL,
  title         VARCHAR(200) NOT NULL,
  description   TEXT NULL,
  status        ENUM('TODO','IN_PROGRESS','BLOCKED','COMPLETED') NOT NULL DEFAULT 'TODO',
  due_at        DATETIME(3) NULL,
  created_by    BIGINT UNSIGNED NOT NULL,
  assigned_to   BIGINT UNSIGNED NULL,
  created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                           ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (task_id),
  KEY idx_tasks_workspace (workspace_id),
  KEY idx_tasks_workspace_status (workspace_id, status),
  KEY idx_tasks_workspace_assignee (workspace_id, assigned_to),
  KEY idx_tasks_due (workspace_id, due_at),
  KEY idx_tasks_created_by (created_by),
  CONSTRAINT fk_tasks_workspace
    FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_tasks_created_by
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_tasks_assigned_to
    FOREIGN KEY (assigned_to) REFERENCES users(user_id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Comments Table
CREATE TABLE task_comments (
  comment_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  task_id      BIGINT UNSIGNED NOT NULL,
  author_id    BIGINT UNSIGNED NOT NULL,
  body         TEXT NOT NULL,
  created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (comment_id),
  KEY idx_comments_task_time (task_id, created_at),
  CONSTRAINT fk_comments_task
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_comments_author
    FOREIGN KEY (author_id) REFERENCES users(user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Task Activity Table
CREATE TABLE task_activity (
  activity_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  task_id      BIGINT UNSIGNED NOT NULL,
  actor_id     BIGINT UNSIGNED NOT NULL,
  action_type  ENUM('CREATED','UPDATED','STATUS_CHANGED','ASSIGNED','COMMENTED') NOT NULL,
  metadata     JSON NULL,
  created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (activity_id),
  KEY idx_activity_task_time (task_id, created_at),
  CONSTRAINT fk_activity_task
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_activity_actor
    FOREIGN KEY (actor_id) REFERENCES users(user_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
