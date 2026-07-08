package com.Orka.entities.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum  TASK_RUN_AUTH_ROLE {
    ASSIGNEE,
    VIEWER
}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRunAuthorization implements Authorization {
private String taskRunId;
private String username;
private TASK_RUN_AUTH_ROLE authRole;
}
