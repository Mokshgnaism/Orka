package com.Orka.entities.definition;

import com.Orka.entities.authorization.Authorization;

import java.util.List;
import java.util.UUID;

public class TaskDefinition {

    private UUID id;

    private UUID workflowDefinitionId;

    private String name;

    private String description;

    private List<Authorization> authorizations;

    private List<StateDefinition> states;

}