package com.Orka.service;

import com.Orka.ENUM.AuthEnums.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.Store.Store;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.repository.TaskDefinitionRepository;

import java.util.*;

public class AuthAnsweringService {
    private final TaskDefinitionRepository taskDefinitionRepository;

    public AuthAnsweringService(TaskDefinitionRepository taskDefinitionRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
    }

    public List<UUID> getAllWorkflowDefinitionIdsVisibleToUser(String username){
        Set<WorkflowDefinitionAuthorization> usersAuthorisedWorkflowDefinitions;
        Store.LOCK.readLock().lock();
        try{
            usersAuthorisedWorkflowDefinitions = Store.workflowDefinitionAuthorizations.getOrDefault(username,new HashSet<WorkflowDefinitionAuthorization>());
        }
        finally {
            Store.LOCK.readLock().unlock();
        }
        return usersAuthorisedWorkflowDefinitions.stream().map(WorkflowDefinitionAuthorization::getId).toList();
    }
    public List<UUID>getAllTaskDefinitionIdsVisibleToUser(String username){
        return null;
    }
    private List<UUID>getAllTaskRunIdsVisibleToUser(String username){
         return null;
    }
    private List<UUID>getAllWorkflowRunIdsVisibleToUser(String username){
        return null;
    }
    private boolean canUserProvideInput(String username,UUID taskDefinitionId){
        Set<TaskDefinitionAuthorization> authorizations = new HashSet<>();
            Store.LOCK.readLock().lock();
            try {
                 authorizations =
                        Store.taskDefinitionAuthorizations.getOrDefault(
                                username,
                                Collections.emptySet());
            } finally {
                Store.LOCK.readLock().unlock();
            }
        return authorizations.stream()
                .anyMatch(auth ->
                        auth.getTaskDefinition().getId().equals(taskDefinitionId) && auth.getAuthRole()== TASK_DEFINITION_AUTH_ROLE.CANDIDATE_ASSIGNEE);
    }
}
