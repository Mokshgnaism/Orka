package com.Orka.entities.datareference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Builder
@Setter
public class StateOutputReference implements DataReference {

    private UUID taskDefinitionId;

    private UUID stateDefinitionId;
    private String taskDefinitionName;

    private String stateDefinitionName;

    /**
     * JSON Path inside the Output JSON
     */
    private String jsonPath;

}