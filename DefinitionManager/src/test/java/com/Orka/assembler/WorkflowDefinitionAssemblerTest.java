package com.Orka.assembler;

import com.Orka.ENUM.AuthEnums.WORKFLOW_DEFINITION_AUTH_ROLE;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.WorkflowDefinitionAuthRole;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.testutil.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowDefinitionAssemblerTest {

    @Test
    void assembleWorkflowDefinition_success() {
        WorkflowDefinition result = WorkflowDefinitionAssembler.assembleWorkflowDefinition(
                TestFixtures.minimalWorkflowRequest());

        assertNotNull(result);
        assertEquals("Test Workflow", result.getName());
        assertEquals("Workflow for unit tests", result.getDescription());
        assertEquals(1, result.getVersion());
        assertEquals("testCreator", result.getCreatorName());
        assertEquals(1, result.getTasks().size());
        assertEquals(1, result.getVariableDefinitions().size());
        assertEquals("startState", result.getStartState().getName());
        assertEquals("task1", result.getTasks().getFirst().getName());
        assertEquals(1, result.getAuthorizationList().size());
        assertEquals(WORKFLOW_DEFINITION_AUTH_ROLE.VIEWER, result.getAuthorizationList().getFirst().getAuthRole());
        assertSame(result, result.getTasks().getFirst().getWorkflowDefinition());
        assertSame(result, result.getVariableDefinitions().getFirst().getWorkflowDefinition());
    }

    @Test
    void assembleWorkflowDefinition_mapsConfiguratorRole() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .clearAuthorizations()
                .addAuthorizations(com.Orka.apiContract.generated.WorkflowDefinitionAuthorization.newBuilder()
                        .setUsername("configurator")
                        .setAuthorization(WorkflowDefinitionAuthRole.WORKFLOW_DEFINITION_CONFIGURATOR)
                        .build())
                .build();

        WorkflowDefinition result = WorkflowDefinitionAssembler.assembleWorkflowDefinition(request);

        assertEquals(WORKFLOW_DEFINITION_AUTH_ROLE.CONFIGURATOR, result.getAuthorizationList().getFirst().getAuthRole());
    }

    @Test
    void assembleWorkflowDefinition_throwsWhenStartTaskMissing() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .setStartTaskDefinitionName("missingTask")
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> WorkflowDefinitionAssembler.assembleWorkflowDefinition(request));

        assertEquals("Task definition id is empty", ex.getMessage());
    }

    @Test
    void assembleWorkflowDefinition_throwsWhenStartStateMissing() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .setStartStateDefinitionName("missingState")
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> WorkflowDefinitionAssembler.assembleWorkflowDefinition(request));

        assertEquals("State definition id is empty", ex.getMessage());
    }

    @Test
    void assembleWorkflowDefinition_throwsWhenDuplicateTaskNames() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .addTaskDefinitions(TestFixtures.minimalTask("task1", "otherState"))
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> WorkflowDefinitionAssembler.assembleWorkflowDefinition(request));

        assertEquals("Duplicated task Names", ex.getMessage());
    }

    @Test
    void assembleWorkflowDefinition_throwsWhenDuplicateVariableNames() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .addWorkflowVariables(com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                        .setName("myVar")
                        .setVariableType(com.Orka.apiContract.generated.VariableType.INTEGER)
                        .build())
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> WorkflowDefinitionAssembler.assembleWorkflowDefinition(request));

        assertEquals("Duplicated state Names", ex.getMessage());
    }

    @Test
    void assembleWorkflowDefinition_withOptionalConditions() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest().toBuilder()
                .setFailedCondition(com.Orka.apiContract.generated.Condition.newBuilder()
                        .setConditionName("failed")
                        .setExpression("F")
                        .build())
                .setCompletedCondition(com.Orka.apiContract.generated.Condition.newBuilder()
                        .setConditionName("completed")
                        .setExpression("C")
                        .build())
                .setRunningCondition(com.Orka.apiContract.generated.Condition.newBuilder()
                        .setConditionName("running")
                        .setExpression("R")
                        .build())
                .build();

        WorkflowDefinition result = WorkflowDefinitionAssembler.assembleWorkflowDefinition(request);

        assertNotNull(result.getFailedCondition());
        assertNotNull(result.getCompletedCondition());
        assertNotNull(result.getRunningCondition());
        assertEquals("failed", result.getFailedCondition().getName());
        assertEquals("completed", result.getCompletedCondition().getName());
        assertEquals("running", result.getRunningCondition().getName());
    }
}
