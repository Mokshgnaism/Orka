package com.Orka.controller.RestRunController;

import com.Orka.apiContract.generated.StartWorkflowRunRequest;
import com.Orka.apiContract.generated.StartWorkflowRunResponse;
import com.Orka.grpc.client.RunManagerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestRunController {
    private final RunManagerClient runManagerClient;
    public RestRunController(RunManagerClient runManagerClient) {
        this.runManagerClient = runManagerClient;
    }
    @PostMapping("/api/run/startWorkflow")
    public ResponseEntity<StartWorkflowRunResponse> startWorkflow(@RequestBody StartWorkflowRunRequest request){
        System.out.println("REST id = '" + request.getId() + "'");
        StartWorkflowRunResponse response = runManagerClient.startWorkflowRun(request);
        if(!response.getError().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
