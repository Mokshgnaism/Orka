package com.Orka.internal;




import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Builder
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
