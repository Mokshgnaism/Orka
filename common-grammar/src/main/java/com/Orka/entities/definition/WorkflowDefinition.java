package com.Orka.entities.definition;

import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.internal.VariableDefinition;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WorkflowDefinition {

    private UUID id;

    private String name;

    private String description;

    private Integer version;

    private String creatorName;

    private UUID startTaskDefinitionId;

    private UUID startStateDefinitionId;

    private List<TaskDefinition> tasks;

    private List<VariableDefinition> variableDefinitions;

    private List<WorkflowDefinitionAuthorization> authorizationList;

}