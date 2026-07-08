package com.Orka.entities.definition;
import com.Orka.internal.VariableDefinition;
import java.util.List;
import java.util.UUID;

public class ScriptDefinition {
    UUID scriptDefinitionId;
    String scriptName;
    Integer version;
    String dockerImage;
    String entryCommand;
    Integer timeout ; // in milliseconds
    List<VariableDefinition> environmentVariables;
}
