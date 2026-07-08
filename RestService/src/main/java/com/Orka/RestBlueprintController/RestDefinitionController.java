package com.Orka.RestBlueprintController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.Orka.apiContract.generated.*;
@RestController("/api/definition")
public class RestDefinitionController {
    @PostMapping("/create/workflowDefinition")
    public ResponseEntity<CreateWorkflowDefinitionResponse> createWorkflowDefinition(@RequestBody CreateWorkflowDefinitionRequest createWorkflowDefinitionRequest) {
         var response = CreateWorkflowDefinitionResponse.newBuilder().setWorkflowDefinitionId("1").build();
         return ResponseEntity.ok(response);
    }
}
