package com.Orka.assembler;

import com.Orka.apiContract.generated.StateDefinition;
import com.Orka.apiContract.generated.TaskDefinitionAuthRole;
import com.Orka.apiContract.generated.TaskDefinitionAuthorization;
import com.Orka.apiContract.generated.VariableDefinition;
import com.Orka.entities.authorization.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.entities.definition.TaskDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
public class TaskDefinitionAssembler {
    public  static TaskDefinition assemble(com.Orka.apiContract.generated.TaskDefinition taskDefinitionDTO, UUID workflowDefinitionId) {
        String taskName =  taskDefinitionDTO.getName();
        String taskDescription =  taskDefinitionDTO.getDescription();

        UUID taskDefinitionId = UUID.randomUUID();


//        --------------------BUILDING AUTH --------------------------------------------
        List<TaskDefinitionAuthorization>taskDefinitionAuthDTOS = taskDefinitionDTO.getAuthorizationsList();
        List<com.Orka.entities.authorization.TaskDefinitionAuthorization> createdAuths = new ArrayList<>();
        for(var authDTO : taskDefinitionAuthDTOS){
            createdAuths.add(
                    com.Orka.entities.authorization.TaskDefinitionAuthorization.builder()
                            .taskDefinitionId(taskDefinitionId)
                            .username(authDTO.getUsername())
                            .authRole(ENUM_MAPPER(authDTO.getAuthorization()))
                            .build()
            );
        }
//        --------------------------------------AUTH COMPLETED------------------------------------



//        -----------------------------------BUILDING states---------------------------------------
//        this state definition list is for dtos not the original statedef class .
        List<StateDefinition>stateDefinitionDTOS = taskDefinitionDTO.getStatesList();
        List<com.Orka.entities.definition.StateDefinition> createdStates = stateDefinitionDTOS.stream().map(stateDefDto -> StateDefinitionAssembler.assemble(stateDefDto,taskDefinitionId,workflowDefinitionId)).toList();




        TaskDefinition task =  TaskDefinition.builder()
                .id(taskDefinitionId)
                .workflowDefinitionId(workflowDefinitionId)
                .name(taskDefinitionDTO.getName())
                .description((taskDefinitionDTO.getName()))
                .authorizations(createdAuths)
                .states(createdStates)
                .build();
        log.debug(
                "Task '{}' assembled with {} states.",
                task.getName(),
                task.getStates().size()
        );
        return task;
    }
    private static TASK_DEFINITION_AUTH_ROLE ENUM_MAPPER(TaskDefinitionAuthRole ROLE_DTO){
        if(ROLE_DTO.equals(TaskDefinitionAuthRole.TASK_DEFINITION_CONFIGURATOR)){
            return TASK_DEFINITION_AUTH_ROLE.CONFIGURATOR;
        }
        else if(ROLE_DTO.equals(TaskDefinitionAuthRole.TASK_DEFINITION_MANAGER))
            return TASK_DEFINITION_AUTH_ROLE.MANAGER;
        else if(ROLE_DTO.equals(TaskDefinitionAuthRole.TASK_DEFINITION_VIEWER))
            return TASK_DEFINITION_AUTH_ROLE.VIEWER;
        else
            return null;
    }
}
