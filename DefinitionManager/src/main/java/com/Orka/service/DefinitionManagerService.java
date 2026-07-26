package com.Orka.service;

import com.Orka.apiContract.generated.*;
import com.Orka.assembler.WorkflowDefinitionAssembler;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.publisher.KafkaPublisher;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.util.ProtoEnumMapper;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DefinitionManagerService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final KafkaPublisher kafkaPublisher;

    public DefinitionManagerService(WorkflowDefinitionRepository workflowDefinitionRepository, KafkaPublisher kafkaPublisher) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.kafkaPublisher = kafkaPublisher;
    }

    public void createWorkflowDefinition(
            CreateWorkflowDefinitionRequest request,
            StreamObserver<CreateWorkflowDefinitionResponse> responseObserver) {
        try {

            WorkflowDefinition createdWorkflowDefinition =
                    WorkflowDefinitionAssembler.assembleWorkflowDefinition(request);

            workflowDefinitionRepository.save(createdWorkflowDefinition);

            CreateWorkflowDefinitionResponse response =
                    CreateWorkflowDefinitionResponse.newBuilder()
                            .setWorkflowDefinitionId(createdWorkflowDefinition.getId().toString())
                            .build();
            kafkaPublisher.publish_workflowDefinitionStarted(createdWorkflowDefinition.getId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            // Log the complete stack trace on the server
            e.printStackTrace();

            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    public void getAllWorkflowDefinitions(StreamObserver<GetAllWorkflowDefinitionsResponse>streamObserver){
        List<WorkflowDefinitionDTO>workflowDefinitionDTOS = workflowDefinitionRepository.findAll().
                stream().
                map(this::convertToDTO)
                .toList();
        GetAllWorkflowDefinitionsResponse response = GetAllWorkflowDefinitionsResponse.newBuilder().addAllWorkflowDefinitions(workflowDefinitionDTOS).build();
        streamObserver.onNext(response);
        streamObserver.onCompleted();
    }
    private WorkflowDefinitionDTO convertToDTO(WorkflowDefinition workflowDefinition){
        List<WorkflowDefinitionAuthorization>workflowDefinitionAuthorizationDTOS =
                workflowDefinition.getAuthorizationList().stream().map(
                        this::mapToAuthDTO
                ).toList();
        return WorkflowDefinitionDTO.newBuilder()
                .setId(workflowDefinition.getId().toString())
                .setName(workflowDefinition.getName())
                .setCreatedBy(workflowDefinition.getCreatorName())
                .setStartStateId(workflowDefinition.getStartState().getId().toString())
                .setStartStateName(workflowDefinition.getStartState().getName())
                .setStartTaskId(workflowDefinition.getStartTaskDefinitionId().toString())
                .setStartTaskName(workflowDefinition.getStartState().getTaskDefinition().getName())
                .addAllAuthorizations(workflowDefinitionAuthorizationDTOS)
                .build();
    }
    private WorkflowDefinitionAuthorization mapToAuthDTO(com.Orka.entities.authorization.WorkflowDefinitionAuthorization workflowDefinitionAuthorization ){
        return  WorkflowDefinitionAuthorization.newBuilder().
                setAuthorization(ProtoEnumMapper.toProto(workflowDefinitionAuthorization.getAuthRole(),WorkflowDefinitionAuthRole.class))
                .setUsername(workflowDefinitionAuthorization.getUsername())
                .build();
    }

}
