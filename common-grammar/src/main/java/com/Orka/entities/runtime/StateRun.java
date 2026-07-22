package com.Orka.entities.runtime;

import com.Orka.ENUM.status.StateRunStatus;
import com.Orka.entities.definition.StateDefinition;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class StateRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private TaskRun taskRun;

    @ManyToOne(fetch = FetchType.LAZY,cascade =  CascadeType.ALL)
    private StateDefinition stateDefinition;

    private StateRunStatus status;

    private Instant enteredAt;

    private Instant exitedAt;

    /**
     * Actual runtime input.
     */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode input;

    /**
     * Actual runtime output.
     */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode output;

}
