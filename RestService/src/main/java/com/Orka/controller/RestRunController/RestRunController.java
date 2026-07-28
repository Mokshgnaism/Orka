package com.Orka.controller.RestRunController;

import com.Orka.apiContract.generated.*;
import com.Orka.grpc.client.RunManagerClient;
import com.Orka.user.User;
import lombok.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.ProtectionDomain;
import java.util.Objects;

@RestController
public class RestRunController {
    private final RunManagerClient runManagerClient;
    public RestRunController(RunManagerClient runManagerClient) {
        this.runManagerClient = runManagerClient;
    }
    @PostMapping("/api/run/startWorkflow")
    public ResponseEntity<StartWorkflowRunResponse> startWorkflow(@RequestBody StartWorkflowRunRequest request) {
        StartWorkflowRunResponse response = runManagerClient.startWorkflowRun(request);
        if (!response.getError().isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/run/provideInput")
    public ResponseEntity<ProtoHttpResponse>provideInput(@RequestBody ProvideInputRequest request){
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if(user==null || !Objects.equals(user.getUsername(), request.getUsername()))
            return ResponseEntity.status(401).body(
                    ProtoHttpResponse.newBuilder().setStatusCode(401).setError("your username does not match with ours.").build()
            );
        ProvideInputResponse response = runManagerClient.provideInput(request);
        return ResponseEntity.status(response.getHttpResponse().getStatusCode()).body(response.getHttpResponse());
    }

    @GetMapping("/api/run/tasks")
    public ResponseEntity<GetAllTaskRunsResponse> getAllTaskRuns() {
        @NonNull
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());

        var request = GetAllTaskRunsRequest.newBuilder().setUsername(user.getUsername()).build();

         GetAllTaskRunsResponse response =  runManagerClient.getAllTaskRuns(request);
//         TODO : add a http response to the request body and make sure the same code passes through
         return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/run/workflows")
    public ResponseEntity<GetAllWorkflowRunsResponse> getAllWorkflowRuns() {
        @NonNull
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
        var request = GetAllWorkflowRunsRequest.newBuilder().setUsername(user.getUsername()).build();

        GetAllWorkflowRunsResponse response = runManagerClient.getAllWorkflowRuns(request);

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/api/run/task/{id}")
    public ResponseEntity<GetSingleTaskRunResponse>getSingleTaskRun(@PathVariable String id){
        @NonNull
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
        var request = GetSingleTaskRunRequest.newBuilder().setId(id).setUsername(user.getUsername()).build();
        GetSingleTaskRunResponse response = runManagerClient.getTaskRunById(request);
        int statusCode = response.getHttpResponse().getStatusCode();
        return ResponseEntity.status(statusCode).body(response);
    }

    @GetMapping("/api/run/workflow/{id}")
    public ResponseEntity<GetSingleWorkflowRunResponse>getSingleWorkflowRun(@PathVariable String id){
        @NonNull
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
        var request = GetSingleWorkflowRunRequest.newBuilder().setId(id).setUsername(user.getUsername()).build();
        GetSingleWorkflowRunResponse response = runManagerClient.getWorkflowRunById(request);
        int statusCode = response.getHttpResponse().getStatusCode();
        return ResponseEntity.status(statusCode).body(response);
    }

}

// 1) create an input proto . for