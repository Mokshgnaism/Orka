package com.Orka.entities.definition;

import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
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

    // Keep for backward compatibility for now
    private UUID startStateDefinitionId;

    // New JPA relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "start_state_definition_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
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
}