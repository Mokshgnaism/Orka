package com.Orka.entities.runtime;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
@Getter
public class TaskRun {

    private UUID id;

    private UUID workflowRunId;

    private UUID taskDefinitionId;

    /**
     * Current active state.
     */
    private UUID currentStateDefinitionId;

    private UUID currentStateRunId;

    private Integer retryCount;

    private TaskRunStatus status;

    private Instant startedAt;

    private Instant completedAt;

    /**
     * Runtime information of every visited state.
     */
    private List<StateRun> stateRuns;


}
enum TaskRunStatus {

    WAITING,

    READY,

    RUNNING,

    COMPLETED,

    FAILED,

    CANCELLED

}