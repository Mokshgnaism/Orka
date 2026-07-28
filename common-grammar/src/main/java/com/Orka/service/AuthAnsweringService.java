package com.Orka.service;

import com.Orka.ENUM.AuthEnums.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.repository.TaskDefinitionRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.repository.WorkflowRunRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
@Transactional
@Service
public class AuthAnsweringService {
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskRunRepository taskRunRepository;
    private final WorkflowRunRepository workflowRunRepository;

    public AuthAnsweringService(TaskDefinitionRepository taskDefinitionRepository, TaskRunRepository taskRunRepository, WorkflowRunRepository workflowRunRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskRunRepository = taskRunRepository;
        this.workflowRunRepository = workflowRunRepository;
    }

    public boolean canUserProvideInput(String username, UUID taskDefinitionId){
        List<TaskDefinitionAuthorization> authorizations =  new ArrayList<>();
        TaskDefinition taskDefinition = taskDefinitionRepository.findById(taskDefinitionId).orElse(null);
        if(taskDefinition==null)
            return false;
        authorizations = taskDefinition.getAuthorizations();
        return authorizations.stream()
                .anyMatch(auth ->
                        auth.getTaskDefinition().getId().equals(taskDefinitionId) && auth.getAuthRole()== TASK_DEFINITION_AUTH_ROLE.CANDIDATE_ASSIGNEE);
    }
    public boolean hasTaskRunAccess(TaskRun taskRun,UUID id, String username){
        if(taskRun==null){
            taskRun = taskRunRepository.findById(id).orElse(null);
        }
        if(taskRun==null){
            return false;
        }
        boolean isAuthorized = taskRun.getAuthorizations().stream().anyMatch(auth->auth.getUsername().equals(username));
        if(isAuthorized){
            return true;
        }
        isAuthorized = isAuthorized || taskRun.getWorkflowRun().getAuthorizations().stream().anyMatch(auth-> auth.getUsername().equals(username));
        if(isAuthorized){
            return true;
        }
        isAuthorized = isAuthorized ||taskRun.getTaskDefinition().getAuthorizations().stream().anyMatch(auth->auth.getUsername().equals(username));
        if(isAuthorized){
            return true;
        }
        isAuthorized = isAuthorized || taskRun.getWorkflowRun().getWorkflowDefinition().getAuthorizationList().stream().anyMatch(auth->auth.getUsername().equals(username));

        return isAuthorized;
    }

    public boolean hasWorkflowRunAccess(WorkflowRun workflowRun, UUID id, String username){
        if(workflowRun==null){
            workflowRun = workflowRunRepository.findById(id).orElse(null);
        }
        if(workflowRun==null){
            return false;
        }
        if(workflowRun.getAuthorizations().stream().anyMatch(auth->auth.getUsername().equals(username))){
            return true;
        }
        return workflowRun.getWorkflowDefinition().getAuthorizationList().stream().anyMatch(auth->auth.getUsername().equals(username));
    }
}
