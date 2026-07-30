package com.Orka.assembler;

import com.google.protobuf.Value;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VariableDefinitionAssemblerTest {

    @Test
    void testAssemble() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.VariableDefinition dto = com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                .setName("testVar")
                .setVariableType(com.Orka.apiContract.generated.VariableType.STRING)
                .setDefaultValue(Value.newBuilder().setStringValue("defaultVal").build())
                .build();

        com.Orka.internal.VariableDefinition result = VariableDefinitionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals("testVar", result.getName());
        assertEquals(workflowId, result.getWorkflowDefinitionId());
        assertEquals(com.Orka.ENUM.typeEnums.VariableType.STRING, result.getType());
        assertEquals("defaultVal", result.getDefaultValue().asText());
    }

    @Test
    void testAssembleInteger() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.VariableDefinition dto = com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                .setName("intVar")
                .setVariableType(com.Orka.apiContract.generated.VariableType.INTEGER)
                .setDefaultValue(Value.newBuilder().setNumberValue(42).build())
                .build();

        com.Orka.internal.VariableDefinition result = VariableDefinitionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals("intVar", result.getName());
        assertEquals(com.Orka.ENUM.typeEnums.VariableType.INTEGER, result.getType());
        assertEquals(42, result.getDefaultValue().asInt());
    }

    @Test
    void testAssembleBoolean() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.VariableDefinition dto = com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                .setName("boolVar")
                .setVariableType(com.Orka.apiContract.generated.VariableType.BOOLEAN)
                .setDefaultValue(Value.newBuilder().setBoolValue(true).build())
                .build();

        com.Orka.internal.VariableDefinition result = VariableDefinitionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertTrue(result.getDefaultValue().asBoolean());
        assertEquals(com.Orka.ENUM.typeEnums.VariableType.BOOLEAN, result.getType());
    }

    @Test
    void testAssembleJson() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.VariableDefinition dto = com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                .setName("jsonVar")
                .setVariableType(com.Orka.apiContract.generated.VariableType.JSON)
                .setDefaultValue(Value.newBuilder().setStringValue("{\"key\":\"value\"}").build())
                .build();

        com.Orka.internal.VariableDefinition result = VariableDefinitionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals(com.Orka.ENUM.typeEnums.VariableType.JSON, result.getType());
        assertEquals("value", result.getDefaultValue().get("key").asText());
    }

    @Test
    void testAssembleEnumType() {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.VariableDefinition dto = com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                .setName("enumVar")
                .setVariableType(com.Orka.apiContract.generated.VariableType.ENUM)
                .setDefaultValue(Value.newBuilder().setStringValue("OPTION_A").build())
                .build();

        com.Orka.internal.VariableDefinition result = VariableDefinitionAssembler.assemble(dto, workflowId);

        assertNotNull(result);
        assertEquals(com.Orka.ENUM.typeEnums.VariableType.ENUM, result.getType());
        assertEquals("OPTION_A", result.getDefaultValue().asText());
    }
}
