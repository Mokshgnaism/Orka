package com.Orka.entities.authorization;
enum TASK_DEFINITION_AUTH_ROLE{
    CANDIDATE_ASSIGNEE,
    VIEWER,
    MANAGER,
    CONFIGURATOR
}
public class TaskDefinitionAuthorization implements Authorization {
    private String taskDefinitionId;
    private String username;
    private TASK_DEFINITION_AUTH_ROLE authRole;
}
