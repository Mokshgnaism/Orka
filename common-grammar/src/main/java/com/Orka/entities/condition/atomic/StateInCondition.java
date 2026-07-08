package com.Orka.entities.condition.atomic;
import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
public class StateInCondition
        implements AtomicCondition {
    private String name;

    private UUID workflowDefinitionId;

    private UUID taskDefinitionId;

    private UUID stateDefinitionId;

    @Override
    public boolean evaluate(
            EvaluationContext context,

            Repository taskRunRepository
    ) {
        WorkflowRun workflowRun = context.getWorkflowRun();


        WorkflowDefinition workflowDefinition = context.getWorkflowDefinition();

        // all the task Runs inside the workflow run
        List<TaskRun> taskRuns = workflowRun.getTaskRuns();

        // out of all the task runs inside the workflow run we need to get the task run which we actually need
        List<TaskRun>temp = taskRuns.stream().filter(tr -> tr.getTaskDefinitionId().equals(taskDefinitionId)).collect(Collectors.toList());
        TaskRun taskRun = temp.getFirst();


        UUID currentStateDefinitionId= taskRun.getCurrentStateDefinitionId();

        return currentStateDefinitionId.equals(stateDefinitionId);
    }

}