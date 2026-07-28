package com.Orka.entities.definition;

import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.condition.Condition;
import com.Orka.internal.VariableDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "workflow_definition")
public class WorkflowDefinition {

    @Id
    private UUID id;

    private String name;

    private String description;

    private Integer version;

    @Column(name = "created_by")
    private String creatorName;

    // Keep for backward compatibility for now
    private UUID startTaskDefinitionId;

    // New JPA relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "start_state_definition_id",
            referencedColumnName = "id"
    )
    private StateDefinition startState;

    @OneToMany(
            mappedBy = "workflowDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TaskDefinition> tasks = new ArrayList<>();

    @OneToMany(
            mappedBy = "workflowDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VariableDefinition> variableDefinitions = new ArrayList<>();

    @OneToMany(
            mappedBy = "workflowDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkflowDefinitionAuthorization> authorizationList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "failed_condition_id")
    private Condition failedCondition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "completed_condition_id")
    private Condition completedCondition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "running_condition_id")
    private Condition runningCondition;

}