package com.Orka.controller.RestBlueprintController;

import com.Orka.grpc.client.DefinitionManagerClient;
import com.Orka.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.Orka.apiContract.generated.*;

import java.util.Objects;

@RestController("/api/definition")
public class RestDefinitionController {
    private final DefinitionManagerClient definitionManagerClient;

    public RestDefinitionController(DefinitionManagerClient definitionManagerClient) {
        this.definitionManagerClient = definitionManagerClient;
    }

    @PostMapping("/create/workflowDefinition")
    public ResponseEntity<CreateWorkflowDefinitionResponse> createWorkflowDefinition(@RequestBody CreateWorkflowDefinitionRequest createWorkflowDefinitionRequest) {

        // check the username to be correct from frontend if trying to impersonate reject

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if(!user.getUsername().equals(createWorkflowDefinitionRequest.getUsername())){
            return ResponseEntity.badRequest().body(CreateWorkflowDefinitionResponse.newBuilder().setJson("{error:wrong username sent by frontend}").build());
        }
        CreateWorkflowDefinitionResponse response = definitionManagerClient.createWorkflowDefinition(createWorkflowDefinitionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/definition/workflowdefinitions")
    public ResponseEntity<GetAllWorkflowDefinitionsResponse> getAllWorkflowDefintions(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if(user==null){
//            WILL NOT REACH HERE
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var req = GetAllWorkflowDefinitionsRequest.newBuilder().setUsername(user.getUsername()).build();
        GetAllWorkflowDefinitionsResponse response = definitionManagerClient.getAllWorkflowDefinitions(req);

        return ResponseEntity.status(200).body(response);
    }

}
