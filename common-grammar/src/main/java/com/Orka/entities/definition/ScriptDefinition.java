package com.Orka.entities.definition;
import com.Orka.internal.VariableDefinition;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ScriptDefinition {
    UUID id;
    UUID stateDefinitionId;
    String scriptName;
    Integer version;
    String dockerImage;
    String entryCommand;
    Integer timeout ; // in milliseconds
    List<VariableDefinition> environmentVariables;
}
