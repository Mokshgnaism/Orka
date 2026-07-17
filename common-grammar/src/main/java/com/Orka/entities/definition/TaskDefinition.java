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
    private UUID workflowDefinitionId;

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
            orphanRemoval = true
    )
    @JoinColumn(name = "task_definition_id")
    @Builder.Default
    private List<TaskDefinitionAuthorization> authorizations = new ArrayList<>();
}