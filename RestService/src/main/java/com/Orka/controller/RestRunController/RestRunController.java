package com.Orka.controller.RestRunController;

import com.Orka.apiContract.generated.*;
import com.Orka.grpc.client.RunManagerClient;
import com.Orka.user.User;
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
}

// 1) create an input proto . for