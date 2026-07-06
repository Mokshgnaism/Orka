package com.Orka.entities.datareference;

import java.util.UUID;

public class StateDataReference implements DataReference {

    /**
     * Task from which the data should be read.
     */
    private UUID taskDefinitionId;

    /**
     * State inside that task.
     */
    private UUID stateDefinitionId;

    /**
     * JSON Path inside the state's runtime JSON.
     *
     * Example:
     * $.report.score
     */
    private String jsonPath;

}