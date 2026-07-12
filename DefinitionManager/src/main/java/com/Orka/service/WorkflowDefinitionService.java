package com.Orka.service;

import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;

public interface WorkflowDefinitionService {
    CreateWorkflowDefinitionResponse createWorkflowDefinition(CreateWorkflowDefinitionRequest request);
}
