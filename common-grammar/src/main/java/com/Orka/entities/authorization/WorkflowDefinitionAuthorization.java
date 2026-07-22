package com.Orka.entities.authorization;

import com.Orka.ENUM.AuthEnums.WORKFLOW_DEFINITION_AUTH_ROLE;
import com.Orka.entities.definition.WorkflowDefinition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class WorkflowDefinitionAuthorization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // keep for backward comaptibility
    @Column(name = "workflow_definition_id_deprecated")
    private UUID workflowDefinitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkflowDefinition workflowDefinition;

    private String username;

    @Enumerated(EnumType.STRING)
    private WORKFLOW_DEFINITION_AUTH_ROLE authRole;
}
