package com.Orka.service;
import com.Orka.apiContract.generated.StartWorkflowRunRequest;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.Assembler.WorkflowRunAssembler;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.repository.WorkflowRunRepository;
import com.Orka.service.KafkaService.WorkflowEventPublisher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class  WorkflowRunService {
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;

    private final WorkflowEventPublisher workflowEventPublisher;
    public WorkflowRunService(WorkflowDefinitionRepository workflowDefinitionRepository, WorkflowRunRepository workflowRunRepository, WorkflowEventPublisher workflowEventPublisher) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;

        this.workflowRunRepository = workflowRunRepository;
        this.workflowEventPublisher = workflowEventPublisher;
    }
    public  WorkflowRun startWorkflowRun(StartWorkflowRunRequest request){
        log.info("Start Workflow Run  reached to service");
        log.info("Request id = '{}'", request.getId());
        UUID workflowDefinitionId = UUID.fromString(request.getId());
        Optional<WorkflowDefinition> workflowOptional = workflowDefinitionRepository.findById(workflowDefinitionId);
        WorkflowDefinition workflowDefinition  = null;

//        TODO : one call to authorization manager here and then we have to check if the current user has access to this workflow definition
        if(workflowOptional.isPresent()){
            workflowDefinition = workflowOptional.get();
            log.info("Workflow Def present");
        }
        if(workflowDefinition == null){
            log.info("Workflow Def not found");
            throw new RuntimeException("Workflow Definition is not found for your authorization");
        }
        log.info("Workflow Run sent to assemble");
        WorkflowRun workflowRun = WorkflowRunAssembler.assemble(workflowDefinition,request.getWorkflowAuthorizationList(),request.getTaskRunAuthorizationList());
        log.info("workflow run assembled");
        try{
            log.info("trying to save workflow run");
            workflowRunRepository.save(workflowRun);
            log.info("workflow run saved");
            Optional<WorkflowRun>workflowRunSaved= workflowRunRepository.findById(workflowRun.getId());
            if(workflowRunSaved.isEmpty()){
                throw new RuntimeException("Workflow not saved correctly");
            }
            workflowRun = workflowRunSaved.get();
        }catch (Exception e){
            log.info("workflow run could not be saved");
            log.error(e.getMessage(),e);
            log.error("workflow run failed to save");
            e.printStackTrace();
            return null;
        }
        return workflowRun;
    }
}
