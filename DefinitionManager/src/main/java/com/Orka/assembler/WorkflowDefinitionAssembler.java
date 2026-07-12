package com.Orka.assembler;

import com.Orka.apiContract.generated.*;
import com.Orka.assembler.utilResolver.WorkflowReferenceResolver;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.authorization.WORKFLOW_DEFINITION_AUTH_ROLE;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class WorkflowDefinitionAssembler {
    public static WorkflowDefinition  assembleWorkflowDefinition(CreateWorkflowDefinitionRequest request) throws IllegalArgumentException {
        String workflowDefinitionName = request.getName();
        String workflowDefinitionDescription = request.getDescription();
        String creatorName = request.getCreatorName();
        Integer version = request.getVersion();

        String startTaskDefinitionName = request.getStartTaskDefinitionName();
        UUID workflowDefinitionId  = UUID.randomUUID();

        String startStateDefinitionName = request.getStartStateDefinitionName();

        List<com.Orka.apiContract.generated.WorkflowDefinitionAuthorization> workflowDefinitionAuthorizationDTOS = request.getAuthorizationsList();


//      this task definition is the generated proto one not the original one please don't mistake it for that .
        List<TaskDefinition> taskDefinitionsDTOS = request.getTaskDefinitionsList();

        log.info("Assemble Workflow Definition {} ", workflowDefinitionName );

//        log.debug("[DTO] TaskDTOS received at workflow assembler : {}",taskDefinitionsDTOS);
//        this is also generated proto not the original one .
        List<VariableDefinition> variableDefinitionDTOS = request.getWorkflowVariablesList();
//        log.debug("[DTO] VariableDTOS received at workflow assembler : {}",variableDefinitionDTOS);

        List<WorkflowDefinitionAuthorization> authorizations = new ArrayList<>();
        for(var authDTO :  workflowDefinitionAuthorizationDTOS){
            authorizations.add(
                    WorkflowDefinitionAuthorization.builder()
                            .workflowDefinitionId(workflowDefinitionId)
                            .username(authDTO.getUsername())
                            .authRole(WORKFLOW_AUTH_ENUM_MAPPER(authDTO.getAuthorization()))
                            .build()
            );
        }
//        log.debug("[authDTO] received auth DTOS :  {}",workflowDefinitionAuthorizationDTOS);

        log.debug("[CREATED] AUTH : {} ",authorizations);



        List<com.Orka.entities.definition.TaskDefinition> createdTaskDefinitions = taskDefinitionsDTOS.stream().map(taskDefinition -> TaskDefinitionAssembler.assemble(taskDefinition,workflowDefinitionId)).toList();
        List<com.Orka.internal.VariableDefinition> createdVariableDefinitions = variableDefinitionDTOS.stream().map(variableDefinitionDTO -> VariableDefinitionAssembler.assemble(variableDefinitionDTO,workflowDefinitionId)).toList();

        List<UUID>startTaskDefinitionIds = createdTaskDefinitions.stream().filter(taskDefinition -> startTaskDefinitionName.equals(taskDefinition.getName())).map(com.Orka.entities.definition.TaskDefinition::getId).toList();

        List<com.Orka.entities.definition.StateDefinition> stateDefinitionsList =
                createdTaskDefinitions.stream()
                        .flatMap(taskDefinition -> taskDefinition.getStates().stream())
                        .filter(stateDefinition -> startStateDefinitionName.equals(stateDefinition.getName()))
                        .toList();


        if(startTaskDefinitionIds.isEmpty()){
            throw new IllegalArgumentException("Task definition id is empty");
        }
        if(stateDefinitionsList.isEmpty()){
            throw new IllegalArgumentException("State definition id is empty");
        }
        log.info("[CREATED] start State Name {}",stateDefinitionsList.getFirst().getName());

        Set<String>taskNames = createdTaskDefinitions.stream().map(com.Orka.entities.definition.TaskDefinition::getName).collect(Collectors.toSet());
        if(taskNames.size() != createdTaskDefinitions.size()){
            throw new IllegalArgumentException("Duplicated task Names");
        }

        Set<String>stateNames =  createdVariableDefinitions.stream().map(com.Orka.internal.VariableDefinition::getName).collect(Collectors.toSet());
        if(stateNames.size() != createdVariableDefinitions.size()){
            throw new IllegalArgumentException("Duplicated state Names");
        }

        createdTaskDefinitions.stream().map(td -> {log.debug("[CREATED] task : \n" +
                        "[Task Name] : {} \n" +
                        "[workflowDefinitionId] : {}] \n" +
                        "[description] : {}] \n"
                ,td.getName(),td.getWorkflowDefinitionId(),td.getDescription());
            return null;
        });

        createdVariableDefinitions.stream().map(variableDefinition ->
                {
                    log.debug("[variable name]:{}\n" +
                            "[workflowDefinitionId]:{}\n]" +
                            "[id]" +
                            "[type]:{}\n]" +
                            "[defaultValue]:{}\n]",
                            variableDefinition.getName(),variableDefinition.getId(),variableDefinition.getType(),variableDefinition.getDefaultValue());
                    return null;
                });


        WorkflowDefinition workflowDefinition =  WorkflowDefinition.builder()
                .id(workflowDefinitionId)
                .name(workflowDefinitionName)
                .description(workflowDefinitionDescription)
                .version(version)
                .creatorName(creatorName)
                .startTaskDefinitionId(startTaskDefinitionIds.getFirst())
                .startStateDefinitionId(stateDefinitionsList.getFirst().getId())
                .authorizationList(authorizations)
                .tasks(createdTaskDefinitions)
                .variableDefinitions(createdVariableDefinitions)
                .build();
//        resolve the ids which were not filled by us in the dfs (state input reference and state output reference only contained ids until this point)
        WorkflowReferenceResolver.resolve(workflowDefinition);
        return workflowDefinition;
    }
    private static WORKFLOW_DEFINITION_AUTH_ROLE WORKFLOW_AUTH_ENUM_MAPPER(WorkflowDefinitionAuthRole DTO_ROLE) {
        if(DTO_ROLE.equals(WorkflowDefinitionAuthRole.WORKFLOW_DEFINITION_CONFIGURATOR))
            return WORKFLOW_DEFINITION_AUTH_ROLE.CONFIGURATOR;
        else if(DTO_ROLE.equals(WorkflowDefinitionAuthRole.WORKFLOW_DEFINITION_MANAGER))
            return WORKFLOW_DEFINITION_AUTH_ROLE.MANAGER;
        else if(DTO_ROLE.equals(WorkflowDefinitionAuthRole.WORKFLOW_DEFINITION_VIEWER))
            return WORKFLOW_DEFINITION_AUTH_ROLE.VIEWER;
        else
            return null;
    }
}
