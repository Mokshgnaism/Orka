package com.Orka.assembler;

import com.Orka.ENUM.AuthEnums.TASK_DEFINITION_AUTH_ROLE;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.internal.VariableDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskDefinitionAssemblerTest {

    @Test
    void assemble_buildsTaskWithStatesAndAuthorizations() throws Exception {
        UUID workflowId = UUID.randomUUID();
        VariableDefinition variable = TestFixtures.internalVariable(workflowId, "myVar");

        TaskDefinition result = TaskDefinitionAssembler.assemble(
                TestFixtures.minimalTask("task1", "startState"),
                workflowId,
                List.of(variable));

        assertNotNull(result);
        assertEquals("task1", result.getName());
        assertEquals(workflowId, result.getWorkflowDefinitionId());
        assertEquals(1, result.getStates().size());
        assertEquals("startState", result.getStates().getFirst().getName());
        assertEquals(1, result.getAuthorizations().size());
        assertEquals("taskUser", result.getAuthorizations().getFirst().getUsername());
        assertEquals(TASK_DEFINITION_AUTH_ROLE.MANAGER, result.getAuthorizations().getFirst().getAuthRole());
        assertSame(result, result.getAuthorizations().getFirst().getTaskDefinition());
        assertSame(result, result.getStates().getFirst().getTaskDefinition());
    }

    @Test
    void assemble_mapsViewerRole() throws Exception {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.TaskDefinition dto = TestFixtures.minimalTask("task1", "startState")
                .toBuilder()
                .clearAuthorizations()
                .addAuthorizations(com.Orka.apiContract.generated.TaskDefinitionAuthorization.newBuilder()
                        .setUsername("viewer")
                        .setAuthorization(com.Orka.apiContract.generated.TaskDefinitionAuthRole.TASK_DEFINITION_VIEWER)
                        .build())
                .build();

        TaskDefinition result = TaskDefinitionAssembler.assemble(dto, workflowId, List.of());

        assertEquals(TASK_DEFINITION_AUTH_ROLE.VIEWER, result.getAuthorizations().getFirst().getAuthRole());
    }

    @Test
    void assemble_mapsUnknownRoleToCandidateAssignee() throws Exception {
        UUID workflowId = UUID.randomUUID();
        com.Orka.apiContract.generated.TaskDefinition dto = TestFixtures.minimalTask("task1", "startState")
                .toBuilder()
                .clearAuthorizations()
                .addAuthorizations(com.Orka.apiContract.generated.TaskDefinitionAuthorization.newBuilder()
                        .setUsername("assignee")
                        .setAuthorization(com.Orka.apiContract.generated.TaskDefinitionAuthRole.TASK_UNSPECIFIED)
                        .build())
                .build();

        TaskDefinition result = TaskDefinitionAssembler.assemble(dto, workflowId, List.of());

        assertEquals(TASK_DEFINITION_AUTH_ROLE.CANDIDATE_ASSIGNEE, result.getAuthorizations().getFirst().getAuthRole());
    }
}
