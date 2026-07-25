package com.Orka.grpc;
import com.Orka.apiContract.generated.*;
import com.Orka.apiContract.generated.services.DefinitionManagerGrpc;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.service.DefinitionManagerService;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class DefinitionMangerController
        extends DefinitionManagerGrpc.DefinitionManagerImplBase {
    private final DefinitionManagerService definitionManagerService;


    public DefinitionMangerController(WorkflowDefinitionRepository workflowDefinitionRepository, DefinitionManagerService definitionManagerService) {
        super();
        this.definitionManagerService = definitionManagerService;
    }
//    rpc GetAllWorkflowDefinitions(getAllWorkflowDefinitionsRequest)
//    returns (getAllWorkflowDefinitionsResponse);
    @Override
    public void createWorkflowDefinition(
            CreateWorkflowDefinitionRequest request,
            StreamObserver<CreateWorkflowDefinitionResponse> responseObserver)
    {
            definitionManagerService.createWorkflowDefinition(request,responseObserver);
    }

//    TODO : check why it is wrong .
    public void GetAllWorkflowDefinitions(
            GetAllWorkflowDefinitionsRequest getAllWorkflowDefinitionsRequest,
            StreamObserver<GetAllWorkflowDefinitionsResponse>responseObserver
    ){
        definitionManagerService.getAllWorkflowDefinitions(responseObserver);
    }


}