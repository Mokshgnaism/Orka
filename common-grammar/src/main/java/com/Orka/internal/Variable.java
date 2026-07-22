package com.Orka.internal;

import com.Orka.entities.runtime.WorkflowRun;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Variable {
//    i actually added this name here (it will be the same as the variable definition a bit redundand but handy .
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkflowRun workflowRun;

    @ManyToOne(fetch = FetchType.LAZY)
    private VariableDefinition variableDefinition;

    /**
     * Actual runtime value.
     */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode value;
}
