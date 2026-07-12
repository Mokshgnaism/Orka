package com.Orka.assembler.StateAssemblerUtil;

import com.Orka.entities.datareference.*;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataReferenceAssembler {

    public static DataReference assemble(com.Orka.apiContract.generated.DataReference dataReferenceDTO) {

        log.debug("Assembling DataReference");

        if (dataReferenceDTO.hasConstant()) {

            log.debug("Detected DataReference type : CONSTANT");

            JsonNode jsonValue =
                    JsonUtility.translateFromProtobufValue(
                            dataReferenceDTO.getConstant().getValue());

            log.debug("Constant Value : {}", jsonValue);

            return ConstantReference.builder()
                    .jsonValue(jsonValue)
                    .build();
        }

        else if (dataReferenceDTO.hasStateInput()) {

            log.debug("Detected DataReference type : STATE_INPUT_REFERENCE");

            log.debug(
                    "Task = {}, State = {}, JsonPath = {}",
                    dataReferenceDTO.getStateInput().getTaskDefinitionName(),
                    dataReferenceDTO.getStateInput().getStateDefinitionName(),
                    dataReferenceDTO.getStateInput().getJsonPath());

            // TODO : in the final stage of building since these stuff only fill the names and not the ids .
            // once after the ids are done make sure filling these up with ids.
            return StateInputReference.builder()
                    .stateDefinitionName(dataReferenceDTO.getStateInput().getStateDefinitionName())
                    .taskDefinitionName(dataReferenceDTO.getStateInput().getTaskDefinitionName())
                    .jsonPath(dataReferenceDTO.getStateInput().getJsonPath())
                    .build();
        }

        else if (dataReferenceDTO.hasStateOutput()) {

            log.debug("Detected DataReference type : STATE_OUTPUT_REFERENCE");

            log.debug(
                    "Task = {}, State = {}, JsonPath = {}",
                    dataReferenceDTO.getStateOutput().getTaskDefinitionName(),
                    dataReferenceDTO.getStateOutput().getStateDefinitionName(),
                    dataReferenceDTO.getStateOutput().getJsonPath());

            return StateOutputReference.builder()
                    .stateDefinitionName(dataReferenceDTO.getStateOutput().getStateDefinitionName())
                    .taskDefinitionName(dataReferenceDTO.getStateOutput().getTaskDefinitionName())
                    .jsonPath(dataReferenceDTO.getStateOutput().getJsonPath())
                    .build();
        }

        else if (dataReferenceDTO.hasWorkflowVariable()) {

            log.debug("Detected DataReference type : WORKFLOW_VARIABLE_REFERENCE");

            log.debug(
                    "Workflow Variable = {}",
                    dataReferenceDTO.getWorkflowVariable().getVariableName());

            return WorkflowVariableReference.builder()
                    .variableName(dataReferenceDTO.getWorkflowVariable().getVariableName())
                    .build();
        }

        log.warn("Received UNKNOWN DataReference type.");

        return null;
    }
}