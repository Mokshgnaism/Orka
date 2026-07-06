package com.Orka.entities.definition;

import java.util.UUID;

public class InputDefinition {

    private UUID id;

    /**
     * JSON Schema
     */
    private String jsonSchema;

    /**
     * How every field inside the schema is populated.
     */
    private List<InputBinding> bindings;

}