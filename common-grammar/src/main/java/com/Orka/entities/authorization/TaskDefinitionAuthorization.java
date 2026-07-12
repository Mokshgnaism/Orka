package com.Orka.entities.authorization;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class TaskDefinitionAuthorization implements Authorization {
    private UUID taskDefinitionId;
    private String username;
    private TASK_DEFINITION_AUTH_ROLE authRole;
}
