package com.Orka.entities.definition;

import com.Orka.entities.authorization.Authorization;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDefinition {

    private UUID id;

    private UUID workflowDefinitionId;

    private String name;

    private String description;

    private List<TaskDefinitionAuthorization> authorizations;

    private List<StateDefinition> states;

}