package com.Orka.internal;




import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
@Entity
@Table(name = "variable_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableDefinition {

    @Id
    private UUID id;

    // Keep temporarily for assembler compatibility
    private UUID workflowDefinitionId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "variable_type", nullable = false)
    private VariableType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "default_value", columnDefinition = "jsonb")
    private JsonNode defaultValue;
}