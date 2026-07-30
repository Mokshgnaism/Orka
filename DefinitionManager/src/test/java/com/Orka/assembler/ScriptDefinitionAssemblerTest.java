package com.Orka.assembler;

import com.Orka.assembler.UtilityDefinitionAssemblers.ScriptDefinitionAssembler;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.internal.VariableDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScriptDefinitionAssemblerTest {

    @Test
    void assemble_mapsScriptFieldsAndEnvironmentVariables() throws Exception {
        UUID workflowId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();
        VariableDefinition variable = TestFixtures.internalVariable(workflowId, "myVar");

        ScriptDefinition result = ScriptDefinitionAssembler.assemble(
                TestFixtures.minimalScriptDefinition(),
                workflowId,
                stateId,
                List.of(variable));

        assertNotNull(result);
        assertEquals("test-script", result.getScriptName());
        assertEquals("alpine:latest", result.getDockerImage());
        assertEquals(1, result.getVersion());
        assertEquals("echo hello", result.getEntryCommand());
        assertEquals(30, result.getTimeout());
        assertEquals(1, result.getEnvironmentVariables().size());
        assertSame(variable, result.getEnvironmentVariables().getFirst());
    }
}
