package com.Orka.controller.RestBlueprintController;

import com.Orka.grpc.client.DefinitionManagerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.Orka.apiContract.generated.*;
@RestController("/api/definition")
public class RestDefinitionController {
    private final DefinitionManagerClient definitionManagerClient;

    public RestDefinitionController(DefinitionManagerClient definitionManagerClient) {
        this.definitionManagerClient = definitionManagerClient;
    }

    @PostMapping("/create/workflowDefinition")
    public ResponseEntity<CreateWorkflowDefinitionResponse> createWorkflowDefinition(@RequestBody CreateWorkflowDefinitionRequest createWorkflowDefinitionRequest) {
        CreateWorkflowDefinitionResponse response = definitionManagerClient.createWorkflowDefinition(createWorkflowDefinitionRequest);
        return ResponseEntity.ok(response);
    }
}
