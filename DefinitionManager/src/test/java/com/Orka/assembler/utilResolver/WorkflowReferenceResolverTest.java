package com.Orka.assembler.utilResolver;

import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowReferenceResolverTest {

    @Test
    void resolve_fillsTaskStateAndVariableReferences() throws Exception {
        WorkflowDefinition workflow = TestFixtures.workflowDefinitionForResolver();

        WorkflowReferenceResolver.resolve(workflow);

        WorkflowVariableCondition variableCondition = (WorkflowVariableCondition)
                workflow.getTasks().getFirst().getStates().getFirst()
                        .getConditionToBecomeActive().getAtomicConditions().getFirst();
        assertEquals(workflow.getVariableDefinitions().getFirst().getId(), variableCondition.getVariableDefinitionId());

        StateInputCondition inputCondition = (StateInputCondition)
                workflow.getTasks().getFirst().getStates().getFirst()
                        .getConditionToBecomeActive().getAtomicConditions().get(1);
        StateInputReference inputReference = inputCondition.getReference();
        assertEquals(workflow.getTasks().getFirst().getId(), inputReference.getTaskDefinitionId());
        assertEquals("state1", inputReference.getStateDefinition().getName());

        StateOutputReference bindingSource = (StateOutputReference)
                workflow.getTasks().getFirst().getStates().getFirst()
                        .getInputDefinition().getBindings().getFirst().getSource();
        assertEquals(workflow.getTasks().getFirst().getId(), bindingSource.getTaskDefinitionId());
        assertEquals("state1", bindingSource.getStateDefinition().getName());
    }

    @Test
    void resolve_handlesNullConditionAndInputDefinition() throws Exception {
        WorkflowDefinition workflow = TestFixtures.workflowDefinitionForResolver();
        workflow.getTasks().getFirst().getStates().getFirst().setConditionToBecomeActive(null);
        workflow.getTasks().getFirst().getStates().getFirst().setInputDefinition(null);

        assertDoesNotThrow(() -> WorkflowReferenceResolver.resolve(workflow));
    }
}
