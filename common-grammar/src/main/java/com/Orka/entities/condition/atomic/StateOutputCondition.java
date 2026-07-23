package com.Orka.entities.condition.atomic;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.ENUM.typeEnums.ComparisonOperator;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import com.Orka.util.JsonUtility;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;
@Getter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Setter
@Entity
@DiscriminatorValue("STATE_OUTPUT")
@ToString
public class StateOutputCondition extends AtomicCondition {

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "reference_id")
    private StateOutputReference reference;

    private ComparisonOperator operator;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode expectedValue;

    @Override
    public boolean evaluate(
            EvaluationContext context,
            Repository repo
    ) {

        System.out.println("\n================ STATE OUTPUT CONDITION ================");

        WorkflowRun workflowRun = context.getWorkflowRun();

        UUID taskDefinitionId = this.reference.getTaskDefinitionId();
        UUID stateDefinitionId = this.reference.getStateDefinition().getId();
        String pathInsideInput = this.reference.getJsonPath();

        System.out.println("Looking for");
        System.out.println("Task Definition Id : " + taskDefinitionId);
        System.out.println("State Definition Id: " + stateDefinitionId);
        System.out.println("Json Path          : " + pathInsideInput);

        System.out.println("\n--------------- TASK RUNS ----------------");

        List<TaskRun> taskRuns = workflowRun.getTaskRuns();

        System.out.println("Total TaskRuns = " + taskRuns.size());

        for (TaskRun tr : taskRuns) {
            System.out.println("--------------------------------------");
            System.out.println("TaskRun id          : " + tr.getId());
            System.out.println("Task Definition id  : " + tr.getTaskDefinition().getId());
            System.out.println("Task Definition name: " + tr.getTaskDefinition().getName());
        }

        taskRuns = taskRuns.stream()
                .filter(taskRun ->
                        taskDefinitionId.equals(taskRun.getTaskDefinition().getId()))
                .toList();

        System.out.println("\nMatching TaskRuns = " + taskRuns.size());

        if (taskRuns.isEmpty()) {
            System.out.println("NO MATCHING TASK RUN FOUND");
            return false;
        }

        TaskRun taskRun = taskRuns.getFirst();

        System.out.println("\n--------------- STATE RUNS ----------------");

        List<StateRun> stateRuns = taskRun.getStateRuns();

        System.out.println("Total StateRuns = " + stateRuns.size());

        for (StateRun sr : stateRuns) {
            System.out.println("--------------------------------------");
            System.out.println("StateRun id          : " + sr.getId());
            System.out.println("State Definition id  : " + sr.getStateDefinition().getId());
            System.out.println("State Definition name: " + sr.getStateDefinition().getName());
            System.out.println("Output               : " + sr.getOutput());
        }

        stateRuns = stateRuns.stream()
                .filter(stateRun ->
                        stateDefinitionId.equals(stateRun.getStateDefinition().getId()))
                .toList();

        System.out.println("\nMatching StateRuns = " + stateRuns.size());

        if (stateRuns.isEmpty()) {
            System.out.println("NO MATCHING STATE RUN FOUND");
            return false;
        }

        StateRun stateRun = stateRuns.getFirst();

        JsonNode stateOutput = stateRun.getOutput();

        System.out.println("\n--------------- OUTPUT ----------------");

        System.out.println("Output = " + stateOutput);

        if (stateOutput != null) {
            System.out.println("Output class = " + stateOutput.getClass());

            if (stateOutput.isObject()) {
                System.out.println("Fields:");
                stateOutput.fieldNames().forEachRemaining(System.out::println);
            }
        }

        JsonNode exactVal = JsonUtility.getValue(stateOutput, pathInsideInput);

        System.out.println("\n--------------- EXTRACTION ----------------");

        System.out.println("Extracted Value = " + exactVal);
        System.out.println("Expected Value  = " + expectedValue);
        System.out.println("Operator        = " + operator);

        boolean result = JsonUtility.compare(
                exactVal,
                expectedValue,
                operator
        );

        System.out.println("Comparison Result = " + result);

        System.out.println("========================================================\n");

        return result;
    }

}
