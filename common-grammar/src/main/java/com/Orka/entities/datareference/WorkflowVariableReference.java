package com.Orka.entities.datareference;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Builder
@Getter
public class WorkflowVariableReference implements DataReference {
    private String variableName;
}
