package com.Orka.entities.authorization;

import lombok.Builder;

import java.util.UUID;

@Builder
public class WorkflowDefinitionAuthorization implements Authorization {
    private UUID workflowDefinitionId;
    private String username;
    private WORKFLOW_DEFINITION_AUTH_ROLE authRole;
}
