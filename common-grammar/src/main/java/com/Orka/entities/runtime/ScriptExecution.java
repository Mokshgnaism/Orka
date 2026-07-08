package com.Orka.entities.runtime;

import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;
public class ScriptExecution {

    private UUID id;

    private UUID stateRunId;

    private UUID scriptDefinitionId;

    private Integer attemptNumber;

    private Instant startedAt;

    private Instant completedAt;

    private Integer exitCode;

    private String stdoutLocation;

    private String stderrLocation;
}
