package com.Orka.entities.runtime;

import com.Orka.internal.Variable;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class WorkflowRun {
    private UUID id;

    private UUID workflowDefinitionId;

    private WorkflowRunStatus status;

    private Instant startedAt;

    private Instant completedAt;

    /**
     * Runtime values of workflow variables.
     */
    private List<Variable> variables;

    /**
     * Runtime instances of every task.
     */
    private List<TaskRun> taskRuns;
}
enum WorkflowRunStatus {

    CREATED,

    RUNNING,

    COMPLETED,

    FAILED,

    CANCELLED

}
