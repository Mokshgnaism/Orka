package com.Orka.entities.definition;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Builder
@Getter
public class OutputDefinition {

    private UUID id;

    /**
     * JSON Schema
     */
    private String jsonSchema;

}