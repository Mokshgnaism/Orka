package com.Orka.entities.authorization;

import com.Orka.ENUM.AuthEnums.WORKFLOW_DEFINITION_AUTH_ROLE;
import com.Orka.ENUM.AuthEnums.WORKFLOW_RUN_AUTH_ROLE;
import com.Orka.entities.runtime.WorkflowRun;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class WorkflowRunAuthorization implements Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private WorkflowRun workflowRun;

    private String username;

    private WORKFLOW_RUN_AUTH_ROLE authRole;
}
