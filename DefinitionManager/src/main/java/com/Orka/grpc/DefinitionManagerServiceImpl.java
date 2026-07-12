package com.Orka.grpc;

import com.Orka.Repository.jdbcWorkflowDefinitionRepository;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;
import com.Orka.apiContract.generated.services.DefinitionManagerGrpc;
import com.Orka.assembler.WorkflowDefinitionAssembler;
import com.Orka.entities.definition.WorkflowDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class DefinitionManagerServiceImpl
        extends DefinitionManagerGrpc.DefinitionManagerImplBase {

    private final jdbcWorkflowDefinitionRepository jdbcWorkflowDefinitionRepository;

    public DefinitionManagerServiceImpl(jdbcWorkflowDefinitionRepository jdbcWorkflowDefinitionRepository) {
        super();
        this.jdbcWorkflowDefinitionRepository = jdbcWorkflowDefinitionRepository;
    }

    @Override
    public void createWorkflowDefinition(
            CreateWorkflowDefinitionRequest request,
            StreamObserver<CreateWorkflowDefinitionResponse> responseObserver) {

        try {

            WorkflowDefinition createdWorkflowDefinition =
                    WorkflowDefinitionAssembler.assembleWorkflowDefinition(request);

            jdbcWorkflowDefinitionRepository.save(createdWorkflowDefinition);

            CreateWorkflowDefinitionResponse response =
                    CreateWorkflowDefinitionResponse.newBuilder()
                            .setWorkflowDefinitionId("1")
                            .setJson("yellow")
                            .build();

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
}