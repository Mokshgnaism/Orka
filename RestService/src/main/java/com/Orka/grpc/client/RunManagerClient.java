package com.Orka.grpc.client;

import com.Orka.apiContract.generated.*;
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
    public ProvideInputResponse provideInput(@RequestBody ProvideInputRequest request){
        return stub.provideInput(request);
    }

    public GetAllTaskRunsResponse getAllTaskRuns(GetAllTaskRunsRequest request){
        return stub.getAllTaskRuns(request);
    }

    public GetAllWorkflowRunsResponse getAllWorkflowRuns(GetAllWorkflowRunsRequest request){
        return stub.getAllWorkflowRuns(request);
    }

    public GetSingleTaskRunResponse getTaskRunById(GetSingleTaskRunRequest request){
        return stub.getTaskRunById(request);
    }

    public GetSingleWorkflowRunResponse getWorkflowRunById(GetSingleWorkflowRunRequest request){
        return stub.getWorkflowRunById(request);
    }

}
