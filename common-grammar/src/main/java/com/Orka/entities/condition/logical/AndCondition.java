package com.Orka.entities.condition.logical;

import com.Orka.entities.condition.ConditionNode;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.interfaces.Repository;

public class AndCondition
        implements ConditionNode {

    private ConditionNode left;

    private ConditionNode right;

    @Override
    public boolean evaluate(
            EvaluationContext context,
            Repository repo
    ) {

        return left.evaluate(context,repo) && right.evaluate(context,repo);

    }
}