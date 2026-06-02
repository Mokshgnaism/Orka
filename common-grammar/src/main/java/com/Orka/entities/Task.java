package com.Orka.entities;

import java.util.UUID;

public record Task(
        UUID id,
        UUID rootTaskId,
        UUID workflowRunId,
        UUID workflowBlueprintId
) {
}
