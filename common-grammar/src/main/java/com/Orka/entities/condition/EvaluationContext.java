package com.Orka.entities.condition;

import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.WorkflowRun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EvaluationContext {

    private WorkflowRun workflowRun;
    private WorkflowDefinition workflowDefinition;
}