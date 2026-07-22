package com.Orka.entities.condition;
import com.Orka.util.ConditionEngine;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Builder
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "condition")
    private List<AtomicCondition> atomicConditions;

    private String expression;
//    we are naming this and defaults to stateNameCondition
    private String name;

    public boolean isSatisified(EvaluationContext evaluationContext) {
//        we are sending null since that was meaning less when we started and wont be used any more no problem with that for us .
        return ConditionEngine.evaluateCondition(this,evaluationContext,null);
    }
}