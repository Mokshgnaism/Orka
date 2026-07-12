package com.Orka.assembler.UtilityDefinitionAssemblers;

import com.Orka.assembler.StateAssemblerUtil.DataReferenceAssembler;
import com.Orka.entities.bindings.InputBinding;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class InputBindingAssembler {

    public static InputBinding assemble(com.Orka.apiContract.generated.InputBinding inputBindingDTO) {

        log.debug("Assembling InputBinding");

        var inputBindingBuilder = InputBinding.builder();

        UUID inputBindingId = UUID.randomUUID();

        log.debug("Generated InputBinding ID : {}", inputBindingId);

        inputBindingBuilder.id(inputBindingId);

        String destinationPath = inputBindingDTO.getDestinationPath();

        log.debug("Destination JSON Path : {}", destinationPath);

        inputBindingBuilder.destinationJsonPath(destinationPath);

        log.debug("Assembling DataReference for InputBinding");

        inputBindingBuilder.source(
                DataReferenceAssembler.assemble(
                        inputBindingDTO.getDataReference()));

        InputBinding inputBinding = inputBindingBuilder.build();

        log.debug("""
                InputBinding Assembled
                  id={}
                  destinationPath={}
                  sourceType={}
                """,
                inputBinding.getId(),
                inputBinding.getDestinationJsonPath(),
                inputBinding.getSource() == null
                        ? "NULL"
                        : inputBinding.getSource().getClass().getSimpleName());

        return inputBinding;
    }
}