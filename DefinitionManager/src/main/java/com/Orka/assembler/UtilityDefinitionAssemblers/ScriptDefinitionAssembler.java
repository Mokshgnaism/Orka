package com.Orka.assembler.UtilityDefinitionAssemblers;

import com.Orka.assembler.VariableDefinitionAssembler;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.internal.VariableDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class ScriptDefinitionAssembler {

    public static ScriptDefinition assemble(
            com.Orka.apiContract.generated.ScriptDefinition scriptDefinitionDTO,
            UUID workflowDefinitionId,
            UUID stateDefinitionId,
            List<VariableDefinition> variableDefinitions) {

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

        Map<String, VariableDefinition> nameToVariableDefinition = new HashMap<>();
        variableDefinitions.forEach(variableDefinition -> {nameToVariableDefinition.put(variableDefinition.getName(),variableDefinition);});

        List<VariableDefinition> environmentVariables = environmentVariableDTOS.stream().map(envVarDto->{return nameToVariableDefinition.get(envVarDto.getName());}).collect(Collectors.toList());


        ScriptDefinition scriptDefinition =
                ScriptDefinition.builder()
                        .scriptName(scriptName)
                        .version(version)
                        .entryCommand(scriptDefinitionDTO.getEntryCommand())
                        .dockerImage(dockerImage)
                        .timeout(timeout)
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