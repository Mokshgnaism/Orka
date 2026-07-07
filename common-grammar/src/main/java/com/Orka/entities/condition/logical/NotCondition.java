package com.Orka.entities.condition.logical;
import com.Orka.entities.condition.ConditionNode;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.interfaces.Repository;

public class NotCondition
        implements ConditionNode {

    private ConditionNode child;

    @Override
    public boolean evaluate(
            EvaluationContext context,
            Repository repo
    ) {

        return !child.evaluate(context,repo);

    }
}