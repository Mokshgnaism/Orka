package com.Orka.assembler;

import com.Orka.assembler.UtilityDefinitionAssemblers.InputBindingAssembler;
import com.Orka.entities.bindings.InputBinding;
import com.Orka.entities.datareference.ConstantReference;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputBindingAssemblerTest {

    @Test
    void assemble_mapsDestinationAndSource() {
        InputBinding result = InputBindingAssembler.assemble(
                TestFixtures.inputBinding("$.target.field", TestFixtures.constantDataReference("value")));

        assertNotNull(result);
        assertEquals("$.target.field", result.getDestinationJsonPath());
        assertInstanceOf(ConstantReference.class, result.getSource());
        assertEquals("value", ((ConstantReference) result.getSource()).getJsonValue().asText());
    }
}
