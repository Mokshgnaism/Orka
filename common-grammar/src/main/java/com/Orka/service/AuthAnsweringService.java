package com.Orka.service;

import com.Orka.ENUM.AuthEnums.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.repository.TaskDefinitionRepository;
import jakarta.transaction.Transactional;

import java.util.*;
@Transactional
public class AuthAnsweringService {
    private final TaskDefinitionRepository taskDefinitionRepository;

    public AuthAnsweringService(TaskDefinitionRepository taskDefinitionRepository) {
        this.taskDefinitionRepository = taskDefinitionRepository;
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
}
