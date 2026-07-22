package com.Orka.entities.authorization;

import com.Orka.ENUM.AuthEnums.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.entities.definition.TaskDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TaskDefinitionAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskDefinition taskDefinition;

    //keeping for backward compatibility;
    @Column( name = "task_definition_id_deprecated")
    private UUID taskDefinitionId;


    private String username;

    @Enumerated(EnumType.STRING)
    private TASK_DEFINITION_AUTH_ROLE authRole;
}
