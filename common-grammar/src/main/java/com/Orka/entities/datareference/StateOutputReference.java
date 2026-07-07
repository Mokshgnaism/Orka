package com.Orka.entities.datareference;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StateOutputReference implements DataReference {

    private UUID taskDefinitionId;

    private UUID stateDefinitionId;

    /**
     * JSON Path inside the Output JSON
     */
    private String jsonPath;

}