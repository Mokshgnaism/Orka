package com.Orka.entities.definition;

import com.Orka.entities.condition.Condition;
import java.util.UUID;

import javax.annotation.Nullable;
import java.util.List;

public class StateDefinition {

    private UUID id;

    private UUID taskDefinitionId;

    private String name;

    private Integer priority;

    private ORKA_INTERNAL_STATE internalState;

    private Condition conditionToBecomeActive;

    private InputDefinition inputDefinition;

    private OutputDefinition outputDefinition;

    @Nullable
    private ScriptDefinition scriptDefinition;
}