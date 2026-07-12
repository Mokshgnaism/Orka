package com.Orka.assembler;

import com.Orka.internal.VariableDefinition;
import com.Orka.internal.VariableType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class VariableDefinitionAssembler {
    public static VariableDefinition assemble(com.Orka.apiContract.generated.VariableDefinition variableDefinitionDTO,UUID workflowDefinitionId) {
        UUID uuid = UUID.randomUUID();
        VariableType variableType = VariableTypeMapper(variableDefinitionDTO.getVariableType());
        String variableName = variableDefinitionDTO.getName();
        ObjectMapper objectMapper = new ObjectMapper();

        String json = null;
        try {
            json = JsonFormat.printer()
                    .print(variableDefinitionDTO.getDefaultValue());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        JsonNode defaultValue = null;
        try {
            defaultValue = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return VariableDefinition.builder()
                .id(uuid)
                .workflowDefinitionId(workflowDefinitionId)
                .name(variableName)
                .defaultValue(defaultValue)
                .type(variableType)
                .build();

    }
    private static VariableType VariableTypeMapper(com.Orka.apiContract.generated.VariableType variableType){
        if(variableType.equals(com.Orka.apiContract.generated.VariableType.STRING))
            return VariableType.STRING;
        else if(variableType.equals(com.Orka.apiContract.generated.VariableType.INTEGER))
            return VariableType.INTEGER;
        else if(variableType.equals(com.Orka.apiContract.generated.VariableType.BOOLEAN))
            return VariableType.BOOLEAN;
        else if(variableType.equals(com.Orka.apiContract.generated.VariableType.JSON))
            return VariableType.JSON;
        else
            return VariableType.ENUM;
    }
}
