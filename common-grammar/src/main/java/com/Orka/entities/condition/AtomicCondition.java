package com.Orka.entities.condition;

import com.Orka.interfaces.Repository;

import java.util.UUID;

public interface AtomicCondition {
//    public UUID workflowDefinitionId;
    String getName()
    boolean evaluate(EvaluationContext context, Repository repo);
}