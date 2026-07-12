package com.Orka.entities.datareference;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Builder
@Setter
public final class StateInputReference implements DataReference {

    private UUID taskDefinitionId;

    private String taskDefinitionName;

    private String stateDefinitionName;

    private UUID stateDefinitionId;

    /**
     * JSON Path inside the Input JSON
     */
    private String jsonPath;

}