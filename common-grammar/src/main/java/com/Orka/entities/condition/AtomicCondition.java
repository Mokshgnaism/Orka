package com.Orka.entities.condition;

import com.Orka.interfaces.Repository;


public interface AtomicCondition {
//    public UUID workflowDefinitionId;
    String getName();
    boolean evaluate(EvaluationContext context, Repository repo);
}