package com.Orka.entities.datareference;

import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;
import java.util.UUID;
@Getter
@SuperBuilder(toBuilder = true)
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("STATE_OUTPUT")
public class StateOutputReference extends DataReference {

    private UUID taskDefinitionId;

    @ManyToOne(fetch = FetchType.LAZY,
    cascade = CascadeType.ALL)
    private StateDefinition stateDefinition;


    private String taskDefinitionName;

    private String stateDefinitionName;

    /**
     * JSON Path inside the Output JSON
     */
    private String jsonPath;

    @Transient
    @Override
    public JsonNode getValue(EvaluationContext evaluationContext) {
        WorkflowRun workflowRun = evaluationContext.getWorkflowRun();

        Optional<TaskRun> taskRunOptional = workflowRun.getTaskRuns().stream().filter(taskRun1 ->  taskDefinitionId.equals(taskRun1.getTaskDefinition().getId())).findFirst();
        if(taskRunOptional.isEmpty()){
            throw new RuntimeException("No task definition found with id " + taskDefinitionId);
        }
        TaskRun taskRun = taskRunOptional.get();
        StateRun stateRun = taskRun.getStateRuns().stream().filter(sr->stateDefinitionName.equals(sr.getStateDefinition().getName())).findFirst().get();
        JsonNode output = stateRun.getOutput();
        return JsonUtility.getValue(output,jsonPath);
    }

}