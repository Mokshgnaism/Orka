package com.Orka.entities.datareference;

import tools.jackson.databind.JsonNode;

import java.util.UUID;

public class ConstantReference implements DataReference {
    /**
     * Stored as JSON.
     */
    private JsonNode jsonValue;

}