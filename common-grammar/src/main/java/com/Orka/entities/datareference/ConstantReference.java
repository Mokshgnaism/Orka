package com.Orka.entities.datareference;

import lombok.Getter;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;
@Getter
public class ConstantReference implements DataReference {
    /**
     * Stored as JSON.
     */
    private JsonNode jsonValue;

}