package com.Orka.assembler;

import com.Orka.ENUM.typeEnums.ORKA_INTERNAL_STATE;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.internal.VariableDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StateDefinitionAssemblerTest {

    @Test
    void assemble_withoutScript() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID workflowId = UUID.randomUUID();
        VariableDefinition variable = TestFixtures.internalVariable(workflowId, "myVar");

        StateDefinition result = StateDefinitionAssembler.assemble(
                TestFixtures.minimalState("startState"),
                taskId,
                workflowId,
                List.of(variable));

        assertNotNull(result);
        assertEquals("startState", result.getName());
        assertEquals(1, result.getPriority());
        assertEquals(ORKA_INTERNAL_STATE.RUNNING, result.getInternalState());
        assertEquals(taskId, result.getTaskDefinitionId());
        assertNotNull(result.getConditionToBecomeActive());
        assertNotNull(result.getInputDefinition());
        assertNotNull(result.getOutputDefinition());
        assertNull(result.getScriptDefinition());
    }

    @Test
    void assemble_withScript() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID workflowId = UUID.randomUUID();
        VariableDefinition variable = TestFixtures.internalVariable(workflowId, "myVar");

        com.Orka.apiContract.generated.StateDefinition dto = TestFixtures.minimalState("scriptState")
                .toBuilder()
                .setScriptDefinition(TestFixtures.minimalScriptDefinition())
                .build();

        StateDefinition result = StateDefinitionAssembler.assemble(dto, taskId, workflowId, List.of(variable));

        assertNotNull(result.getScriptDefinition());
        ScriptDefinition script = result.getScriptDefinition();
        assertEquals("test-script", script.getScriptName());
        assertSame(result, script.getStateDefinition());
    }
}
