package com.Orka.entities.condition.atomic;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.ComparisonOperator;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import com.Orka.internal.Variable;
import com.Orka.util.JsonUtility;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

public class WorkflowVariableCondition
        implements AtomicCondition {

    private String name;

    private UUID workflowDefinitionId;

    private String variableName;

    private ComparisonOperator operator;

    private JsonNode expectedValue;

    @Override
    public boolean evaluate(
            EvaluationContext context,
            Repository repo
    ) {

        WorkflowRun workflowRun = context.getWorkflowRun();
        List<Variable> variables = workflowRun.getVariables();
        variables = variables.stream().filter(var -> variableName.equals(var.getName())).toList();
        return JsonUtility.compare(variables.getFirst().getValue(),this.expectedValue,this.operator);
    }

}
