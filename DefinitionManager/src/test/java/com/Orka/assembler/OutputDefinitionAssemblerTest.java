package com.Orka.assembler;

import com.Orka.assembler.UtilityDefinitionAssemblers.OutputDefinitionAssembler;
import com.Orka.entities.definition.OutputDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OutputDefinitionAssemblerTest {

    @Test
    void assemble_mapsJsonSchema() {
        OutputDefinition result = OutputDefinitionAssembler.assemble(TestFixtures.minimalOutputDefinition());

        assertNotNull(result);
        assertEquals("{\"type\":\"object\"}", result.getJsonSchema());
    }
}
