package com.Orka.grpc.client;

import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;
import com.Orka.apiContract.generated.services.DefinitionManagerGrpc;
import org.springframework.stereotype.Service;

@Service
public class DefinitionManagerClient {
    private final DefinitionManagerGrpc.DefinitionManagerBlockingStub stub;

    public DefinitionManagerClient(DefinitionManagerGrpc.DefinitionManagerBlockingStub stub) {
        this.stub = stub;
    }
    public CreateWorkflowDefinitionResponse createWorkflowDefinition(CreateWorkflowDefinitionRequest request){
        return stub.createWorkflowDefinition(request);
    }
}
