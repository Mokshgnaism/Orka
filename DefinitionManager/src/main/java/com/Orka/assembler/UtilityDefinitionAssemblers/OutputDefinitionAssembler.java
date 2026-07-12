package com.Orka.assembler.UtilityDefinitionAssemblers;

import com.Orka.entities.definition.OutputDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class OutputDefinitionAssembler {

    public static OutputDefinition assemble(
            com.Orka.apiContract.generated.OutputDefinition outputDefinitionDTO) {

        log.debug("Assembling OutputDefinition");

        UUID outputDefinitionId = UUID.randomUUID();

        log.debug("Generated OutputDefinition ID : {}", outputDefinitionId);

        log.debug("Output JSON Schema : {}", outputDefinitionDTO.getJsonSchema());

        OutputDefinition outputDefinition =
                OutputDefinition.builder()
                        .id(outputDefinitionId)
                        .jsonSchema(outputDefinitionDTO.getJsonSchema())
                        .build();

        log.debug("""
                OutputDefinition Assembled
                  id={}
                  schemaLength={}
                """,
                outputDefinition.getId(),
                outputDefinition.getJsonSchema() == null
                        ? 0
                        : outputDefinition.getJsonSchema().length());

        return outputDefinition;
    }
}