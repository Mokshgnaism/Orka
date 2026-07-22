package com.Orka.entities.datareference;
import com.Orka.entities.condition.EvaluationContext;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "data_reference_type")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class DataReference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @Transient
    public JsonNode getValue(EvaluationContext evaluationContext) {
        return null;
    };
}