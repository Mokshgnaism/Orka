package com.Orka.assembler;

import com.Orka.assembler.StateAssemblerUtil.DataReferenceAssembler;
import com.Orka.entities.datareference.*;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataReferenceAssemblerTest {

    @Test
    void assemble_constantReference() {
        DataReference result = DataReferenceAssembler.assemble(
                TestFixtures.constantDataReference("hello"));

        assertInstanceOf(ConstantReference.class, result);
        assertEquals("hello", ((ConstantReference) result).getJsonValue().asText());
    }

    @Test
    void assemble_stateInputReference() {
        DataReference result = DataReferenceAssembler.assemble(
                TestFixtures.stateInputDataReference("task1", "state1", "$.path"));

        assertInstanceOf(StateInputReference.class, result);
        StateInputReference ref = (StateInputReference) result;
        assertEquals("task1", ref.getTaskDefinitionName());
        assertEquals("state1", ref.getStateDefinitionName());
        assertEquals("$.path", ref.getJsonPath());
    }

    @Test
    void assemble_stateOutputReference() {
        DataReference result = DataReferenceAssembler.assemble(
                TestFixtures.stateOutputDataReference("task2", "state2", "$.out"));

        assertInstanceOf(StateOutputReference.class, result);
        StateOutputReference ref = (StateOutputReference) result;
        assertEquals("task2", ref.getTaskDefinitionName());
        assertEquals("state2", ref.getStateDefinitionName());
        assertEquals("$.out", ref.getJsonPath());
    }

    @Test
    void assemble_workflowVariableReference() {
        DataReference result = DataReferenceAssembler.assemble(
                TestFixtures.workflowVariableDataReference("myVar"));

        assertInstanceOf(WorkflowVariableReference.class, result);
        assertEquals("myVar", ((WorkflowVariableReference) result).getVariableName());
    }

    @Test
    void assemble_unknownReference_returnsNull() {
        DataReference result = DataReferenceAssembler.assemble(
                com.Orka.apiContract.generated.DataReference.newBuilder().build());

        assertNull(result);
    }
}
