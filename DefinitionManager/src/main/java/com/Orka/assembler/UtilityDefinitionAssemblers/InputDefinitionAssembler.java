package com.Orka.assembler.UtilityDefinitionAssemblers;

import com.Orka.entities.bindings.InputBinding;
import com.Orka.entities.definition.InputDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class InputDefinitionAssembler {

    public static InputDefinition assemble(
            com.Orka.apiContract.generated.InputDefinition inputDefinitionDTO) {

        log.debug("Assembling InputDefinition");

        UUID inputDefinitionId = UUID.randomUUID();

        log.debug("Generated InputDefinition ID : {}", inputDefinitionId);

        List<com.Orka.apiContract.generated.InputBinding> inputBindingDTOS =
                inputDefinitionDTO.getInputBindingsList();

        log.debug("Number of InputBindings : {}", inputBindingDTOS.size());

        log.debug("Input JSON Schema : {}", inputDefinitionDTO.getJsonSchema());

        List<InputBinding> inputBindings =
                inputBindingDTOS.stream()
                        .map(InputBindingAssembler::assemble)
                        .toList();

        InputDefinition inputDefinition =
                InputDefinition.builder()
                        .jsonSchema(inputDefinitionDTO.getJsonSchema())
                        .bindings(inputBindings)
                        .build();

        log.debug("""
                InputDefinition Assembled
                  id={}
                  bindings={}
                  schemaLength={}
                """,
                inputDefinition.getId(),
                inputDefinition.getBindings().size(),
                inputDefinition.getJsonSchema() == null
                        ? 0
                        : inputDefinition.getJsonSchema().length());

         inputDefinition.getBindings().forEach(inputBinding -> {inputBinding.setInputDefinition(inputDefinition);});
         return inputDefinition;
    }
}