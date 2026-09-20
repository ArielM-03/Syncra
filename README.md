# Syncra

Syncra is a multi-tenant team collaboration platform built with Java and Spring Boot. It provides a secured RESTful backend for managing workspaces, tasks, memberships, and role-based access control, modeled after real-world project management tools.

The backend was developed from December 2025 to present as a portfolio project focused on applying enterprise Java patterns, relational database design, authentication, and authorization in a production-minded architecture.

## Features

- JWT-based authentication with BCrypt password hashing
- Role-based access control (RBAC) with OWNER, MEMBER, and VIEWER permission tiers enforced at the service layer
- Full task lifecycle management including creation, assignment, and status transitions (TODO, IN_PROGRESS, BLOCKED, COMPLETED)
- Token-based workspace invitation workflows with expiry validation and single-use enforcement
- Multi-tenant workspace isolation with composite-key membership modeling
- Audit logging schema capturing action types, actors, and JSON metadata for task activity traceability
- Normalized MySQL schema across 7 tables with foreign key constraints, composite indexes, and cascade rules

## Tech Stack

- **Java 21**
- **Spring Boot 4**
- **Spring Security 7**
- **MySQL 8**
- **Spring Data JPA / Hibernate**
- **JSON Web Tokens (jjwt 0.12.6)**
- **Lombok**
- **Maven**

## Architecture

Syncra follows a layered Controller-Service-Repository pattern:

- **Controller** — handles HTTP request routing and response formatting
- **Service** — enforces business logic and RBAC rules
- **Repository** — manages all database access via Spring Data JPA

Authentication is handled through a JWT filter that intercepts every request, validates the token, and sets the security context before the request reaches any controller. All endpoints except `/api/auth/register` and `/api/auth/login` require a valid JWT token.

## Database Schema

| Table | Description |
|---|---|
| `users` | Registered user accounts with BCrypt-hashed passwords |
| `workspaces` | Team workspaces created and owned by users |
| `workspace_members` | Composite-key join table mapping users to workspaces with roles |
| `invites` | Token-based workspace invitations with expiry and acceptance tracking |
| `tasks` | Tasks scoped to a workspace with status, assignment, and audit timestamps |
| `task_comments` | User comments attached to individual tasks |
| `task_activity` | Audit log capturing actions, actors, and JSON metadata per task |

## API Endpoints

**Auth**
- `POST /api/auth/register` — register a new user
- `POST /api/auth/login` — authenticate and receive a JWT token

**Users**
- `GET /api/users` — get all users
- `GET /api/users/{id}` — get user by ID
- `POST /api/users` — create a user
- `DELETE /api/users/{id}` — delete a user

**Workspaces**
- `POST /api/workspaces` — create a workspace
- `GET /api/workspaces/{id}` — get workspace by ID
- `GET /api/workspaces/user/{userId}` — get workspaces by user
- `DELETE /api/workspaces/{id}` — delete a workspace

**Workspace Members**
- `POST /api/workspaces/{id}/members` — add a member with a role
- `GET /api/workspaces/{id}/members` — list members of a workspace
- `GET /api/workspaces/{id}/members/{userId}/role` — get a member's role

**Invitations**
- `POST /api/workspaces/{id}/invites` — send a workspace invitation (OWNER only)
- `POST /api/workspaces/{id}/invites/accept` — accept an invitation via token

**Tasks**
- `POST /api/tasks` — create a task (MEMBER or OWNER)
- `GET /api/tasks/{id}` — get task by ID
- `GET /api/tasks/workspace/{workspaceId}` — get tasks by workspace
- `GET /api/tasks/assignee/{userId}` — get tasks by assignee
- `GET /api/tasks/workspace/{workspaceId}/status/{status}` — filter tasks by status
- `PATCH /api/tasks/{id}/status` — update task status
- `PATCH /api/tasks/{id}/assign` — assign a task to a user
- `DELETE /api/tasks/{id}` — delete a task

## Local Setup

**Prerequisites**
- Java 21
- MySQL 8
- Maven

**Steps**

1. Clone the repository
2. Create the database by running `syncra_v1_schema.sql` against your local MySQL instance
3. Create a dedicated database user:

```sql
CREATE USER 'syncra_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON Syncra.* TO 'syncra_user'@'localhost';
FLUSH PRIVILEGES;
```

4. Set the required environment variable — see Environment Variables below
5. Update `application.properties` with your datasource username if different from `syncra_user`
6. Run the application:
7. The API will be available at `http://localhost:8080`

## Environment Variables

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password for the MySQL syncra_user database account |

Set this in your IDE run configuration or as a system environment variable before running the application. Never commit the actual value to source control.

## Development Notes

All endpoints except the auth routes require a valid JWT token passed as a Bearer token in the Authorization header. Tokens are valid for 24 hours.

RBAC is enforced at the service layer rather than the controller layer, ensuring permission checks apply regardless of how services are invoked. The three permission tiers are OWNER, MEMBER, and VIEWER, with OWNER being the only role permitted to send workspace invitations or manage membership.
