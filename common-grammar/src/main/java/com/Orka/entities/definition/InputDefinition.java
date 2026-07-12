package com.Orka.entities.definition;

import com.Orka.entities.bindings.InputBinding;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
@Builder
@Getter
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