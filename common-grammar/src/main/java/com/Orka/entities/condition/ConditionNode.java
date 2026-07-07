package com.Orka.entities.condition;
import com.Orka.interfaces.Repository;
public interface ConditionNode {

    boolean evaluate(EvaluationContext context,Repository repo);

}