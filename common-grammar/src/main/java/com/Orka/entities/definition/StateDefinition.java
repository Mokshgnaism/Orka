package com.Orka.entities.definition;

import com.Orka.entities.condition.Condition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import javax.annotation.Nullable;
import java.util.List;
@Entity
@Table(name = "state_definition")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "state_definition")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateDefinition {

    @Id
    private UUID id;

    // Temporary
    private UUID taskDefinitionId;

    private String name;

    private Integer priority;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "script_definition_id")
    private ScriptDefinition scriptDefinition;
}