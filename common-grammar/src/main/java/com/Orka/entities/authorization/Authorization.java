package com.Orka.entities.authorization;
enum AUTH_ENTITY {
    TASK_DEFINITION,
    TASK_RUN,
    WORKFLOW_DEFINITION,
    WORKFLOW_RUN
}
public class Authorization {
    public String username;
    public String authRole;
    public AUTH_ENTITY authEntity;
}
