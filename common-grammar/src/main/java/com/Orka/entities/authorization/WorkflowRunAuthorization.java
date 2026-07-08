package com.Orka.entities.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum WORKFLOW_RUN_AUTH_ROLE {
    CONFIGURATOR,
    MANAGER
}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRunAuthorization implements Authorization {
    private String workflowRunId;
    private String username;
    private WORKFLOW_DEFINITION_AUTH_ROLE authRole;
}
