package com.Orka.entities.condition.atomic;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.ComparisonOperator;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.datareference.DataReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import com.Orka.util.JsonUtility;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
public class StateOutputCondition implements AtomicCondition {
    private String name;
    private UUID workflowDefinitionId;

    private StateOutputReference reference;

    private ComparisonOperator operator;

    private JsonNode expectedValue;

    @Override
    public boolean evaluate(
            EvaluationContext context,
            Repository repo
    ) {
//        TODO: reuse the code frm the StateInputCondition . don't rewrite it .
        WorkflowRun workflowRun = context.getWorkflowRun();

        WorkflowDefinition workflowDefinition = context.getWorkflowDefinition();

        // we need to see that the referenced StateInputOf the workflow
        UUID taskDefinitionId = this.reference.getTaskDefinitionId();
        UUID stateDefinitionId = this.reference.getStateDefinitionId();
        String pathInsideInput = this.reference.getJsonPath();

        List<TaskRun> taskRuns = workflowRun.getTaskRuns();
        taskRuns = taskRuns.stream().filter(taskRun -> taskDefinitionId.equals(taskRun.getTaskDefinitionId())).toList();
        TaskRun taskRun = taskRuns.getFirst();

        List<StateRun>stateRuns = taskRun.getStateRuns();
        stateRuns = stateRuns.stream().filter(stateRun -> stateDefinitionId.equals(stateRun.getStateDefinitionId())).toList();
        StateRun stateRun = stateRuns.getFirst();

        JsonNode stateOutput = stateRun.getOutput();

        // will fetch the json node exactly inside the given path .
        JsonNode exactVal = JsonUtility.getValue(stateOutput, pathInsideInput);

        return JsonUtility.compare(exactVal,this.expectedValue,this.operator);

    }

}
