package com.Orka.entities.datareference;

import com.Orka.entities.condition.EvaluationContext;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@Getter
@SuperBuilder(toBuilder = true)
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@DiscriminatorValue("CONSTANT")
public class ConstantReference extends DataReference {
    /**
     * Stored as JSON.
     */
    private JsonNode jsonValue;

    @Override
    @Transient
//    eval context here is not required but for sake of the polymorphic condition we are keeping this
    public JsonNode getValue(EvaluationContext ignored) {
        return jsonValue;
    }
}