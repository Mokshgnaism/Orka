package com.Orka.entities.definition;
import com.Orka.ENUM.typeEnums.ORKA_INTERNAL_STATE;
import com.Orka.entities.condition.Condition;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Table(name = "state_definition")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class StateDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Temporary
    @Column(name = "task_definition_id_deprecated")
    private UUID taskDefinitionId;

    private String name;

    private Integer priority;


    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private TaskDefinition taskDefinition;

    @Enumerated(EnumType.STRING)
    @Column(name = "internal_state")
    private ORKA_INTERNAL_STATE internalState;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "condition_definition_id")
    private Condition conditionToBecomeActive;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "input_definition_id")
    private InputDefinition inputDefinition;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "output_definition_id")
    private OutputDefinition outputDefinition;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "stateDefinition")
    @JoinColumn(name = "script_definition_id")
    private ScriptDefinition scriptDefinition;
}