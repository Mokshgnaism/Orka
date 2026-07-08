package com.Orka.entities.runtime;

import lombok.Getter;
//import tools.jackson.
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;
@Getter
public class StateRun {

    private UUID id;

    private UUID taskRunId;

    private UUID stateDefinitionId;

    private StateRunStatus status;

    private Instant enteredAt;

    private Instant exitedAt;

    /**
     * Actual runtime input.
     */
    private JsonNode input;

    /**
     * Actual runtime output.
     */
    private JsonNode output;

}
enum StateRunStatus {

    WAITING,

    ACTIVE,

    COMPLETED,

    FAILED,

    CANCELLED

}