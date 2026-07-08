package com.Orka.interfaces;

import com.Orka.entities.runtime.TaskRun;

import java.util.UUID;

public interface Repository {
    TaskRun findByWorkflowRunAndTaskDefinition(UUID id, UUID taskDefinitionId);
}
