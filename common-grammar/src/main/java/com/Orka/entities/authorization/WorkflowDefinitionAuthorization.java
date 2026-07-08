package com.Orka.entities.authorization;

enum WORKFLOW_DEFINITION_AUTH_ROLE {
    MANAGER,
    CONFIGURATOR,
    VIEWER
}
public class WorkflowDefinitionAuthorization implements Authorization {
    private String workflowDefinitionId;
    private String username;
    private WORKFLOW_DEFINITION_AUTH_ROLE authRole;

}
