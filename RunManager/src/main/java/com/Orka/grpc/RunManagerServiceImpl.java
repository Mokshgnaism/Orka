package com.Orka.grpc;

import com.Orka.apiContract.generated.ProvideInputRequest;
import com.Orka.apiContract.generated.ProvideInputResponse;
import com.Orka.apiContract.generated.StartWorkflowRunRequest;
import com.Orka.apiContract.generated.StartWorkflowRunResponse;
import com.Orka.apiContract.generated.services.RunManagerGrpc;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.service.TaskRunService;
import com.Orka.service.publisher.WorkflowEventPublisher;
import com.Orka.service.WorkflowRunService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
@GrpcService
@Slf4j
public class RunManagerServiceImpl extends RunManagerGrpc.RunManagerImplBase {
    private final WorkflowRunService workflowRunService;
    private final WorkflowEventPublisher workflowEventPublisher;
    private final TaskRunService taskRunService;


    public RunManagerServiceImpl(WorkflowRunService workflowRunService, WorkflowEventPublisher workflowEventPublisher, TaskRunService taskRunService) {
        this.workflowRunService = workflowRunService;
        this.workflowEventPublisher = workflowEventPublisher;
        this.taskRunService = taskRunService;
    }

    @Override
    public void startWorkflowRun(StartWorkflowRunRequest request,
                                 StreamObserver<StartWorkflowRunResponse> responseObserver) {

        try {

            log.info("Received request");

            WorkflowRun workflowRun =
                    workflowRunService.startWorkflowRun(request);
            if(workflowRun!=null){
                workflowEventPublisher.publish_workflow_created_event(workflowRun.getId());
            }

            responseObserver.onNext(
                    StartWorkflowRunResponse.newBuilder()
                            .setId(workflowRun.getId().toString())
                            .build());

            responseObserver.onCompleted();

        } catch (Exception e) {

            e.printStackTrace();

            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException());
        }
    }

    @Override
    public void provideInput(ProvideInputRequest request, StreamObserver<ProvideInputResponse>streamObserver){
        ProvideInputResponse response = taskRunService.provideInput(request);
        streamObserver.onNext(response);
        streamObserver.onCompleted();
    }
}
