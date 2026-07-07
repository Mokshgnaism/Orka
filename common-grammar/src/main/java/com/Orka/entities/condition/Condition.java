package com.Orka.entities.condition;

import com.Orka.interfaces.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condition {
    private List<AtomicCondition> atomicConditions;
    private String expression;
    private EvaluationContext evaluationContext;
    private Repository repo; // we are not using this and will be dumped as soon as possible .
    // conditions get teh workflowruns and definitions in a very simple manner where we simply pass them stuff dirrectly they donot make calls from the repo so no problme over there.

    public boolean isSatisfied() {
        Map<String, Boolean> conditionResults = new HashMap<>();
        for (AtomicCondition atomicCondition : atomicConditions) {
            conditionResults.put(atomicCondition.getName)
        }
    }
}