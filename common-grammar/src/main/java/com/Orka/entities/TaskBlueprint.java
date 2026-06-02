package com.Orka.entities;

import java.util.UUID;

public record TaskBlueprint(
        UUID id,
        UUID rootBlueprintId,
        UUID workflowBlueprintId
) {
}
