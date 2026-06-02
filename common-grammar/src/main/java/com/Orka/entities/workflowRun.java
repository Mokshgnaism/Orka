package com.Orka.entities;

import java.util.UUID;

public record workflowRun(
        UUID id,
        UUID rootWorkflowRunId,
        UUID workflowBlueprintId
) {
}
