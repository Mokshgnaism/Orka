package com.Orka.Assembler;

import com.Orka.ENUM.AuthEnums.WORKFLOW_RUN_AUTH_ROLE;
import com.Orka.apiContract.generated.WorkflowRunAuthRole;
import com.Orka.entities.authorization.WorkflowRunAuthorization;
import com.Orka.entities.runtime.WorkflowRun;

public class WorkflowRunAuthorizationAssembler {
    public static WorkflowRunAuthorization assemble(com.Orka.apiContract.generated.WorkflowRunAuthorization workflowRunAuthorizationDTO, WorkflowRun workflowRun){
        WorkflowRunAuthorization workflowRunAuthorization = new WorkflowRunAuthorization();
        workflowRunAuthorization.setUsername(workflowRunAuthorizationDTO.getUsername());
        workflowRunAuthorization.setAuthRole(WORKFLOW_RUN_AUTH_MAPPER(workflowRunAuthorizationDTO.getAuthRole()));
        workflowRunAuthorization.setWorkflowRun(workflowRun);
        return workflowRunAuthorization;
    }
    private static WORKFLOW_RUN_AUTH_ROLE WORKFLOW_RUN_AUTH_MAPPER(WorkflowRunAuthRole workflowRunAuthRole){
        if(workflowRunAuthRole.equals(WorkflowRunAuthRole.WORKFLOW_RUN_CONFIGURATOR)){
            return WORKFLOW_RUN_AUTH_ROLE.CONFIGURATOR;
        }else if(workflowRunAuthRole.equals(WorkflowRunAuthRole.WORKFLOW_RUN_MANAGER)){
            return WORKFLOW_RUN_AUTH_ROLE.MANAGER;
        }
        else{
            return null;
        }
    }
}
