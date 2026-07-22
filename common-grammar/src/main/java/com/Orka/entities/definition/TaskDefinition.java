package com.Orka.entities.definition;

import com.Orka.entities.authorization.Authorization;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "task_definition")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDefinition {

    @Id
    private UUID id;

    // Keep temporarily for assembler compatibility
    @Column(name = "workflow_definition_id_depricated")
    private UUID workflowDefinitionId;

//    we are setting all as cascade type = all since we were using some dumb things in code which is too costly to be refactored
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private WorkflowDefinition workflowDefinition;


    private String name;

    private String description;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "task_definition_id")
    @Builder.Default
    private List<StateDefinition> states = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "taskDefinition"
    )
    private List<TaskDefinitionAuthorization> authorizations = new ArrayList<>();
}