package com.Orka.grpc.client;

import com.Orka.apiContract.generated.StartWorkflowRunRequest;
import com.Orka.apiContract.generated.StartWorkflowRunResponse;
import com.Orka.apiContract.generated.services.RunManagerGrpc;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class RunManagerClient {
    private final RunManagerGrpc.RunManagerBlockingStub stub;

    public RunManagerClient(RunManagerGrpc.RunManagerBlockingStub stub) {
        this.stub = stub;
    }
    public StartWorkflowRunResponse startWorkflowRun(@RequestBody StartWorkflowRunRequest request){
        return stub.startWorkflowRun(request);
    }
}
