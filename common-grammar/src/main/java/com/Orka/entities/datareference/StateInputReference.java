package com.Orka.entities.datareference;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class StateInputReference implements DataReference {

    private UUID taskDefinitionId;

    private UUID stateDefinitionId;

    /**
     * JSON Path inside the Input JSON
     */
    private String jsonPath;

}