package com.Orka.entities;

import java.util.UUID;

public record WorkflowBlueprint(
        UUID id,
        UUID rootDefinitionId
) {
}
