package com.Orka.entities.definition;

import com.Orka.entities.authorization.Authorization;
import com.Orka.internal.Variable;

import java.util.List;
import java.util.UUID;

public class WorkflowDefinition {

    private UUID id;

    private String name;

    private String description;

    private Integer version;

    private String creatorName;

    private UUID startTaskDefinitionId;

    private UUID startStateDefinitionId;

    private List<TaskDefinition> tasks;

}