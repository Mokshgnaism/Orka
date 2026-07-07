package com.Orka.internal;




import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public class VariableDefinition {
    private UUID id;

    private UUID workflowDefinitionId;

    private String name;

    private VariableType type;

    /**
     * Optional.
     */
    private JsonNode defaultValue;
}
