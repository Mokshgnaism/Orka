package com.Orka.internal;

import lombok.Getter;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;
@Getter
public class Variable {
//    i actually added this name here (it will be the same as the variable definition a bit redundand but handy .
    private String name;

    private UUID id;

    private UUID workflowRunId;

    private UUID variableDefinitionId;

    /**
     * Actual runtime value.
     */
    private JsonNode value;
}
