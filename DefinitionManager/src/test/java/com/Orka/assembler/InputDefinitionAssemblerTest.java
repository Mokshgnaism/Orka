package com.Orka.assembler;

import com.Orka.assembler.UtilityDefinitionAssemblers.InputDefinitionAssembler;
import com.Orka.entities.definition.InputDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputDefinitionAssemblerTest {

    @Test
    void assemble_withBindings() {
        com.Orka.apiContract.generated.InputDefinition dto =
                com.Orka.apiContract.generated.InputDefinition.newBuilder()
                        .setJsonSchema("{\"type\":\"object\"}")
                        .addInputBindings(TestFixtures.inputBinding("$.a", TestFixtures.constantDataReference("x")))
                        .build();

        InputDefinition result = InputDefinitionAssembler.assemble(dto);

        assertNotNull(result);
        assertEquals("{\"type\":\"object\"}", result.getJsonSchema());
        assertEquals(1, result.getBindings().size());
        assertSame(result, result.getBindings().getFirst().getInputDefinition());
    }

    @Test
    void assemble_withoutBindings() {
        InputDefinition result = InputDefinitionAssembler.assemble(TestFixtures.minimalInputDefinition());

        assertNotNull(result);
        assertTrue(result.getBindings().isEmpty());
    }
}
