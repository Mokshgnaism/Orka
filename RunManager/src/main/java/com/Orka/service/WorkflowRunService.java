package com.Orka.service;
import com.Orka.Assembler.WorkflowRunGraphAssembler.WorkflowRunGraphAssembler;
import com.Orka.apiContract.generated.*;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.Assembler.WorkflowRunAssembler.WorkflowRunAssembler;
import com.Orka.repository.TaskRunRepository;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.repository.WorkflowRunRepository;
import com.Orka.service.publisher.WorkflowEventPublisher;
import com.Orka.util.ProtoEnumMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class  WorkflowRunService {
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunService taskRunService;



    private final WorkflowEventPublisher workflowEventPublisher;
    private final AuthAnsweringService authAnsweringService;

    public WorkflowRunService(WorkflowDefinitionRepository workflowDefinitionRepository, WorkflowRunRepository workflowRunRepository, TaskRunService taskRunService, WorkflowEventPublisher workflowEventPublisher, AuthAnsweringService authAnsweringService) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;

        this.workflowRunRepository = workflowRunRepository;
        this.taskRunService = taskRunService;
        this.workflowEventPublisher = workflowEventPublisher;
        this.authAnsweringService = authAnsweringService;
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

    public GetAllWorkflowRunsResponse getAllWorkflowRuns(String username){
        log.info("[recieved the request for this thing ]");
        List<WorkflowRun>authorizedWorkflowRuns = workflowRunRepository.findAll();
        List<WorkflowRunDTO>workflowRunDTOS = authorizedWorkflowRuns.stream().map(this::toWorkflowRunDTO).toList();
        return GetAllWorkflowRunsResponse.newBuilder().addAllWorkflowRuns(workflowRunDTOS).build();
    }
    private WorkflowRunDTO toWorkflowRunDTO(WorkflowRun workflowRun){
        List<WorkflowRunAuthorization>workflowRunAuthorizationDTOS = workflowRun.getAuthorizations().stream().map(this::toWorkflowRunAuthDTO).toList();
        var builder =  WorkflowRunDTO.newBuilder().setId(workflowRun.getId().toString())
                .setWorkflowDefinitionId(workflowRun.getWorkflowDefinition().getId().toString())
                .setWorkflowDefinitionName(workflowRun.getWorkflowDefinition().getName())
                .setStatus(workflowRun.getStatus().toString())
                .setStartedAt(workflowRun.getStartedAt().toString())
                .setNumberOfTasks(workflowRun.getTaskRuns().size())
                .addAllAuthorizations(workflowRunAuthorizationDTOS);
        if (workflowRun.getCompletedAt() != null) {
            builder.setCompletedAt(workflowRun.getCompletedAt().toString());
        }
        return builder.build();
    }
    private WorkflowRunAuthorization toWorkflowRunAuthDTO(com.Orka.entities.authorization.WorkflowRunAuthorization workflowRunAuthorization){
//        TODO : build a correct mapper and then uncomment the line number 98
        return WorkflowRunAuthorization.newBuilder().setUsername(workflowRunAuthorization.getUsername())
//                .setAuthRole(ProtoEnumMapper.toProto(workflowRunAuthorization.getAuthRole(), WorkflowRunAuthRole.class))
                .build();
    }


    public GetSingleWorkflowRunResponse getWorkflowRunById(GetSingleWorkflowRunRequest request, String username){
        UUID workflowRunId = UUID.fromString(request.getId());
        WorkflowRun workflowRun = workflowRunRepository.findById(workflowRunId).orElse(null);
        if(workflowRun == null){
            return getResponseForSingleWorkflowRun(404,"workflow run not found");
        }
        boolean isAuthorized = authAnsweringService.hasWorkflowRunAccess(workflowRun,workflowRunId,username);
        if(!isAuthorized){
            return getResponseForSingleWorkflowRun(403,"not authorized");
        }
        WorkflowRunDTO workflowRunDTO = toWorkflowRunDTO(workflowRun);
        List<GetSingleTaskRunResponse> taskRuns= workflowRun.getTaskRuns().stream().map(taskRun -> taskRunService.getTaskRunById(taskRun.getId().toString(),username)).toList();
        WorkflowRunGraph workflowRunGraph = WorkflowRunGraphAssembler.assemble(workflowRun);
        return GetSingleWorkflowRunResponse.newBuilder().addAllTaskRuns(taskRuns)
                .setWorkflowRun(workflowRunDTO)
                .setWorkflowRunGraph(workflowRunGraph)
                .setHttpResponse(
                        ProtoHttpResponse.newBuilder()
                                .setStatusCode(200)
                                .setError("")
                                .build()
                )
                .build();
    }

    private static GetSingleWorkflowRunResponse getResponseForSingleWorkflowRun(int statusCode, String message){
        return GetSingleWorkflowRunResponse.newBuilder().setHttpResponse(
                ProtoHttpResponse.newBuilder()
                        .setStatusCode(statusCode)
                        .setError(message)
                        .build()
        ).build();
    }
}
