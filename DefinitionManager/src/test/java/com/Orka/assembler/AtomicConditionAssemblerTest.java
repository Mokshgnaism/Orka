package com.Orka.assembler;

import com.Orka.assembler.StateAssemblerUtil.atomicConditionAssembler;
import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.atomic.StateInCondition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.StateOutputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AtomicConditionAssemblerTest {

    private final UUID workflowId = UUID.randomUUID();

    @Test
    void assemble_stateInCondition() {
        AtomicCondition result = atomicConditionAssembler.assemble(
                TestFixtures.stateInAtomicCondition("inCond", "task1", "state1"),
                workflowId);

        assertInstanceOf(StateInCondition.class, result);
        StateInCondition condition = (StateInCondition) result;
        assertEquals("inCond", condition.getName());
        assertEquals(workflowId, condition.getWorkflowDefinitionId());
        assertEquals("task1", condition.getTaskDefinitionName());
        assertEquals("state1", condition.getStateDefinitionName());
    }

    @Test
    void assemble_stateInputCondition() {
        AtomicCondition result = atomicConditionAssembler.assemble(
                TestFixtures.stateInputAtomicCondition("inputCond"),
                workflowId);

        assertInstanceOf(StateInputCondition.class, result);
        StateInputCondition condition = (StateInputCondition) result;
        assertEquals("inputCond", condition.getName());
        assertEquals(workflowId, condition.getWorkflowDefinitionId());
        assertNotNull(condition.getReference());
        assertNotNull(condition.getOperator());
        assertEquals("expected", condition.getExpectedValue().asText());
    }

    @Test
    void assemble_stateOutputCondition() {
        AtomicCondition result = atomicConditionAssembler.assemble(
                TestFixtures.stateOutputAtomicCondition("outputCond"),
                workflowId);

        assertInstanceOf(StateOutputCondition.class, result);
        StateOutputCondition condition = (StateOutputCondition) result;
        assertEquals("outputCond", condition.getName());
        assertEquals(workflowId, condition.getWorkflowDefinitionId());
        assertNotNull(condition.getReference());
        assertEquals(10, condition.getExpectedValue().asDouble(), 0.001);
    }

    @Test
    void assemble_workflowVariableCondition() {
        AtomicCondition result = atomicConditionAssembler.assemble(
                TestFixtures.workflowVariableAtomicCondition("varCond", "flag"),
                workflowId);

        assertInstanceOf(WorkflowVariableCondition.class, result);
        WorkflowVariableCondition condition = (WorkflowVariableCondition) result;
        assertEquals("varCond", condition.getName());
        assertEquals("flag", condition.getVariableName());
        assertTrue(condition.getExpectedValue().asBoolean());
    }

    @Test
    void assemble_emptyAtomicCondition_returnsNull() {
        AtomicCondition result = atomicConditionAssembler.assemble(
                com.Orka.apiContract.generated.AtomicCondition.newBuilder().setName("empty").build(),
                workflowId);

        assertNull(result);
    }
}
