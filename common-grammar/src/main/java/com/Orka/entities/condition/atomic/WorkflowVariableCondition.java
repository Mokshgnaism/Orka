package com.Orka.entities.condition.atomic;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.ENUM.typeEnums.ComparisonOperator;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import com.Orka.internal.Variable;
import com.Orka.internal.VariableDefinition;
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
@DiscriminatorValue("WORKFLOW_VARIABLE")
@ToString
public class WorkflowVariableCondition
        extends AtomicCondition {

    private String variableName;

    // keeping for backward compatibility;
    @Column(name = "variable_definition_id_deprecated")
    private UUID variableDefinitionId;

    @ManyToOne
    private VariableDefinition variableDefinition;

    private ComparisonOperator operator;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
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
