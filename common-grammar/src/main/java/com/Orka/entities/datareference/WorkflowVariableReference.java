package com.Orka.entities.datareference;

import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.internal.Variable;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Optional;
import java.util.UUID;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@DiscriminatorValue("WORKFLOW_VARIABLE")
public class WorkflowVariableReference extends DataReference {
    private String variableName;
    @Transient
    @Override
    public JsonNode getValue(EvaluationContext evaluationContext) {
        WorkflowRun workflowRun = evaluationContext.getWorkflowRun();

        Variable variable = workflowRun.getVariables().stream().filter(variable1 ->  variableName.equals(variable1.getName())).findFirst().orElse(null);
        if(variable == null)
            return null;
        return variable.getValue();
    }
}
