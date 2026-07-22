package com.Orka.entities.runtime;

import com.Orka.ENUM.status.TaskRunStatus;
import com.Orka.entities.authorization.TaskRunAuthorization;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskRun {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private WorkflowRun workflowRun;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private TaskDefinition taskDefinition;

    private String taskDefinitionName;

    /**
     * Current active state.
     */
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private StateDefinition currentState;

    private String currentStateDefinitionName;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private StateRun currentStateRun;

    private Integer retryCount;

    private TaskRunStatus status;

    private Instant startedAt;

    private Instant completedAt;

    /**
     * Runtime information of every visited state.
     */
    @OneToMany(mappedBy = "taskRun",cascade = CascadeType.ALL)
    private List<StateRun> stateRuns;

    @OneToMany(mappedBy = "taskRun",cascade = CascadeType.ALL)
    private List<TaskRunAuthorization>authorizations;

}
