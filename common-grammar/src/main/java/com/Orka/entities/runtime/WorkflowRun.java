package com.Orka.entities.runtime;

import com.Orka.ENUM.status.WorkflowRunStatus;
import com.Orka.entities.authorization.WorkflowRunAuthorization;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.internal.Variable;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class WorkflowRun {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;

    private WorkflowRunStatus status;

    private Instant startedAt;

    private Instant completedAt;

    /**
     * Runtime values of workflow variables.
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "workflowRun",cascade = CascadeType.ALL)
    private List<Variable> variables;

    /**
     * Runtime instances of every task.
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "workflowRun",cascade = CascadeType.ALL)
    private List<TaskRun> taskRuns;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "workflowRun",cascade = CascadeType.ALL)
    private List<WorkflowRunAuthorization> authorizations;
}
