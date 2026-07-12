package com.Orka.assembler.UtilityDefinitionAssemblers;

import com.Orka.assembler.VariableDefinitionAssembler;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.internal.VariableDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class ScriptDefinitionAssembler {

    public static ScriptDefinition assemble(
            com.Orka.apiContract.generated.ScriptDefinition scriptDefinitionDTO,
            UUID workflowDefinitionId,
            UUID stateDefinitionId) {

        log.debug("Assembling ScriptDefinition");

        UUID scriptDefinitionID = UUID.randomUUID();

        log.debug("Generated ScriptDefinition ID : {}", scriptDefinitionID);

        String scriptName = scriptDefinitionDTO.getScriptName();
        Integer version = scriptDefinitionDTO.getVersion();
        String dockerImage = scriptDefinitionDTO.getDockerImage();
        Integer timeout = scriptDefinitionDTO.getTimeout();

        log.debug("""
                Script Details
                  Name={}
                  Version={}
                  DockerImage={}
                  EntryCommand={}
                  Timeout={}
                """,
                scriptName,
                version,
                dockerImage,
                scriptDefinitionDTO.getEntryCommand(),
                timeout);

        List<com.Orka.apiContract.generated.VariableDefinition> environmentVariableDTOS =
                scriptDefinitionDTO.getEnvironmentVariablesList();

        log.debug("Environment Variables Count : {}", environmentVariableDTOS.size());

        List<VariableDefinition> environmentVariables =
                environmentVariableDTOS.stream()
                        .map(envVar -> VariableDefinitionAssembler.assemble(envVar, workflowDefinitionId))
                        .toList();

        ScriptDefinition scriptDefinition =
                ScriptDefinition.builder()
                        .id(scriptDefinitionID)
                        .scriptName(scriptName)
                        .version(version)
                        .entryCommand(scriptDefinitionDTO.getEntryCommand())
                        .dockerImage(dockerImage)
                        .timeout(timeout)
                        .stateDefinitionId(stateDefinitionId)
                        .environmentVariables(environmentVariables)
                        .build();

        log.debug("""
                ScriptDefinition Assembled
                  id={}
                  scriptName={}
                  dockerImage={}
                  version={}
                  timeout={}
                  environmentVariables={}
                """,
                scriptDefinition.getId(),
                scriptDefinition.getScriptName(),
                scriptDefinition.getDockerImage(),
                scriptDefinition.getVersion(),
                scriptDefinition.getTimeout(),
                scriptDefinition.getEnvironmentVariables().size());

        return scriptDefinition;
    }
}