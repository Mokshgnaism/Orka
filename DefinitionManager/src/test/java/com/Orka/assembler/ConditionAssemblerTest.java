package com.Orka.assembler;

import com.Orka.assembler.StateAssemblerUtil.ConditionAssembler;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.atomic.StateInCondition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConditionAssemblerTest {

    @Test
    void assemble_withAtomicConditions() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.Condition dto = com.Orka.apiContract.generated.Condition.newBuilder()
                .setConditionName("gate")
                .setExpression("A")
                .addAtomicConditions(TestFixtures.stateInAtomicCondition("A", "task1", "state1"))
                .build();

        Condition result = ConditionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals("gate", result.getName());
        assertEquals("A", result.getExpression());
        assertEquals(1, result.getAtomicConditions().size());
        assertInstanceOf(StateInCondition.class, result.getAtomicConditions().getFirst());
        assertSame(result, result.getAtomicConditions().getFirst().getCondition());
    }

    @Test
    void assemble_emptyCondition() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.Condition dto = com.Orka.apiContract.generated.Condition.newBuilder()
                .setConditionName("empty")
                .setExpression("true")
                .build();

        Condition result = ConditionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals("empty", result.getName());
        assertTrue(result.getAtomicConditions().isEmpty());
    }
}
