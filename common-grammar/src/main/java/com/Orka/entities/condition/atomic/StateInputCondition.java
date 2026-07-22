package com.Orka.entities.condition.atomic;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.ENUM.typeEnums.ComparisonOperator;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import com.Orka.util.JsonUtility;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Setter
@Entity
@DiscriminatorValue("STATE_INPUT")
@ToString
public class StateInputCondition
        extends AtomicCondition {


    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "reference_id")
    private StateInputReference reference;

    private ComparisonOperator operator;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode expectedValue;


    @Override
    public boolean evaluate(EvaluationContext context, Repository repo) {
        WorkflowRun workflowRun = context.getWorkflowRun();

        WorkflowDefinition workflowDefinition = context.getWorkflowDefinition();

        // we need to see that the referenced StateInputOf the workflow
        UUID taskDefinitionId = this.reference.getTaskDefinitionId();
        UUID stateDefinitionId = this.reference.getStateDefinition().getId();
        String pathInsideInput = this.reference.getJsonPath();

        List<TaskRun>taskRuns = workflowRun.getTaskRuns();
        taskRuns = taskRuns.stream().filter(taskRun -> taskDefinitionId.equals(taskRun.getTaskDefinition().getId())).toList();
        TaskRun taskRun = taskRuns.getFirst();

        List<StateRun>stateRuns = taskRun.getStateRuns();
        stateRuns = stateRuns.stream().filter(stateRun -> stateDefinitionId.equals(stateRun.getStateDefinition().getId())).toList();
        StateRun stateRun = stateRuns.getFirst();

        JsonNode stateInput = stateRun.getInput();

        JsonNode exactVal = JsonUtility.getValue(stateInput, pathInsideInput);

        return JsonUtility.compare(exactVal,this.expectedValue,this.operator);
    }
}