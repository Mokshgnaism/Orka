package com.Orka.entities.runtime;

import com.Orka.entities.definition.ScriptDefinition;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
public class ScriptExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StateRun stateRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_definition_id")
    private ScriptDefinition scriptDefinition;

    private boolean isRunning ;

    private Integer attemptNumber;

    private Instant startedAt;

    private Instant completedAt;

    private Integer exitCode;

    private String stdout;
    private String stderr;

    public ScriptExecution() {

    }
}
