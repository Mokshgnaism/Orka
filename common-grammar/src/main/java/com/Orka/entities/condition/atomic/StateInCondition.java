package com.Orka.entities.condition.atomic;
import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Getter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Setter
@Entity
@DiscriminatorValue("STATE_IN")
@ToString
public class StateInCondition
        extends AtomicCondition {


    private String taskDefinitionName;

    private String stateDefinitionName;

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
        List<TaskRun>temp = taskRuns.stream().filter(tr -> tr.getTaskDefinitionName().equals(this.taskDefinitionName)).toList();
        TaskRun taskRun = temp.getFirst();


        String currentStateDefinitionName= taskRun.getCurrentStateDefinitionName();
        if(currentStateDefinitionName==null){
            return false;
        }

        return currentStateDefinitionName.equals(stateDefinitionName);
    }

}