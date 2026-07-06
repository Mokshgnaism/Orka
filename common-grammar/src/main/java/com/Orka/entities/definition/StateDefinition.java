package com.Orka.entities.definition;

import com.Orka.entities.condition.Condition;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

enum ORKA_INTERNAL_STATE{
RUNNING,
    COMPLETED,
    ABORTED,
    WAITING_FOR_INPUT,

}
public class StateDefinition {

    private UUID id;

    private UUID taskDefinitionId;

    private String name;

    private Integer priority;

    private ORKA_INTERNAL_STATE internalState;

    private Condition conditionToBecomeActive;

    private InputDefinition inputDefinition;

    private OutputDefinition outputDefinition;

    private ScriptDefinition scriptDefinition;
}