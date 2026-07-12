package com.Orka.service;

import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;
import com.Orka.assembler.WorkflowDefinitionAssembler;
import com.Orka.entities.definition.WorkflowDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {
    @Override
    public CreateWorkflowDefinitionResponse createWorkflowDefinition(CreateWorkflowDefinitionRequest request) {
        WorkflowDefinition workflowDefinition = WorkflowDefinitionAssembler.assembleWorkflowDefinition(request);
        log.info("Workflow Definition Created Successfully for " +
                "[Name] : {}" +
                "[id] : {} ",
                workflowDefinition.getName(),
                workflowDefinition.getId());
        return null;
    }
}
