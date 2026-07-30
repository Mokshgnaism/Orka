package com.Orka.testutil;

import com.Orka.apiContract.generated.AtomicCondition;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.DataReference;
import com.Orka.apiContract.generated.OrkaInternalState;
import com.Orka.apiContract.generated.TaskDefinitionAuthRole;
import com.Orka.apiContract.generated.TaskDefinitionAuthorization;
import com.Orka.apiContract.generated.WorkflowDefinitionAuthRole;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.bindings.InputBinding;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.definition.InputDefinition;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.ENUM.AuthEnums.WORKFLOW_DEFINITION_AUTH_ROLE;
import com.Orka.ENUM.typeEnums.ORKA_INTERNAL_STATE;
import com.Orka.ENUM.typeEnums.VariableType;
import com.Orka.internal.VariableDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;

import java.util.List;
import java.util.UUID;

public final class TestFixtures {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestFixtures() {
    }

    public static com.Orka.apiContract.generated.Condition minimalCondition() {
        return com.Orka.apiContract.generated.Condition.newBuilder()
                .setConditionName("always")
                .setExpression("true")
                .build();
    }

    public static com.Orka.apiContract.generated.InputDefinition minimalInputDefinition() {
        return com.Orka.apiContract.generated.InputDefinition.newBuilder()
                .setJsonSchema("{\"type\":\"object\"}")
                .build();
    }

    public static com.Orka.apiContract.generated.OutputDefinition minimalOutputDefinition() {
        return com.Orka.apiContract.generated.OutputDefinition.newBuilder()
                .setJsonSchema("{\"type\":\"object\"}")
                .build();
    }

    public static com.Orka.apiContract.generated.StateDefinition minimalState(String name) {
        return com.Orka.apiContract.generated.StateDefinition.newBuilder()
                .setName(name)
                .setPriority(1)
                .setInternalState(OrkaInternalState.RUNNING)
                .setCondition(minimalCondition())
                .setInputDefinition(minimalInputDefinition())
                .setOutputDefinition(minimalOutputDefinition())
                .build();
    }

    public static com.Orka.apiContract.generated.TaskDefinition minimalTask(String taskName, String startStateName) {
        return com.Orka.apiContract.generated.TaskDefinition.newBuilder()
                .setName(taskName)
                .setDescription("Task description")
                .addStates(minimalState(startStateName))
                .addAuthorizations(TaskDefinitionAuthorization.newBuilder()
                        .setUsername("taskUser")
                        .setAuthorization(TaskDefinitionAuthRole.TASK_DEFINITION_MANAGER)
                        .build())
                .build();
    }

    public static CreateWorkflowDefinitionRequest minimalWorkflowRequest() {
        return CreateWorkflowDefinitionRequest.newBuilder()
                .setName("Test Workflow")
                .setDescription("Workflow for unit tests")
                .setStartTaskDefinitionName("task1")
                .setStartStateDefinitionName("startState")
                .addTaskDefinitions(minimalTask("task1", "startState"))
                .setVersion(1)
                .setCreatorName("testCreator")
                .addAuthorizations(com.Orka.apiContract.generated.WorkflowDefinitionAuthorization.newBuilder()
                        .setUsername("wfUser")
                        .setAuthorization(WorkflowDefinitionAuthRole.WORKFLOW_DEFINITION_VIEWER)
                        .build())
                .addWorkflowVariables(com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                        .setName("myVar")
                        .setVariableType(com.Orka.apiContract.generated.VariableType.STRING)
                        .setDefaultValue(Value.newBuilder().setStringValue("hello").build())
                        .build())
                .build();
    }

    public static DataReference constantDataReference(String value) {
        return DataReference.newBuilder()
                .setConstant(com.Orka.apiContract.generated.ConstantReference.newBuilder()
                        .setValue(Value.newBuilder().setStringValue(value).build())
                        .build())
                .build();
    }

    public static DataReference stateInputDataReference(String taskName, String stateName, String jsonPath) {
        return DataReference.newBuilder()
                .setStateInput(com.Orka.apiContract.generated.StateInputReference.newBuilder()
                        .setTaskDefinitionName(taskName)
                        .setStateDefinitionName(stateName)
                        .setJsonPath(jsonPath)
                        .build())
                .build();
    }

    public static DataReference stateOutputDataReference(String taskName, String stateName, String jsonPath) {
        return DataReference.newBuilder()
                .setStateOutput(com.Orka.apiContract.generated.StateOutputReference.newBuilder()
                        .setTaskDefinitionName(taskName)
                        .setStateDefinitionName(stateName)
                        .setJsonPath(jsonPath)
                        .build())
                .build();
    }

    public static DataReference workflowVariableDataReference(String variableName) {
        return DataReference.newBuilder()
                .setWorkflowVariable(com.Orka.apiContract.generated.WorkflowVariableReference.newBuilder()
                        .setVariableName(variableName)
                        .build())
                .build();
    }

    public static AtomicCondition stateInAtomicCondition(String name, String taskName, String stateName) {
        return AtomicCondition.newBuilder()
                .setName(name)
                .setStateIn(com.Orka.apiContract.generated.StateInCondition.newBuilder()
                        .setDestinationTaskDefinitionName(taskName)
                        .setRequiredStateDefinitionName(stateName)
                        .build())
                .build();
    }

    public static AtomicCondition stateInputAtomicCondition(String name) {
        return AtomicCondition.newBuilder()
                .setName(name)
                .setStateInput(com.Orka.apiContract.generated.StateInputCondition.newBuilder()
                        .setDestinationTaskDefinitionName("task1")
                        .setExpectedValue(Value.newBuilder().setStringValue("expected").build())
                        .setComparisonOperator(com.Orka.apiContract.generated.ComparisonOperator.EQUAL)
                        .setDataReference(stateInputDataReference("task1", "startState", "$.field"))
                        .build())
                .build();
    }

    public static AtomicCondition stateOutputAtomicCondition(String name) {
        return AtomicCondition.newBuilder()
                .setName(name)
                .setStateOutput(com.Orka.apiContract.generated.StateOutputCondition.newBuilder()
                        .setDestinationTaskDefinitionName("task1")
                        .setExpectedValue(Value.newBuilder().setNumberValue(10).build())
                        .setComparisonOperator(com.Orka.apiContract.generated.ComparisonOperator.GREATER_THAN)
                        .setDataReference(stateOutputDataReference("task1", "startState", "$.count"))
                        .build())
                .build();
    }

    public static AtomicCondition workflowVariableAtomicCondition(String name, String variableName) {
        return AtomicCondition.newBuilder()
                .setName(name)
                .setWorkflowVariable(com.Orka.apiContract.generated.WorkflowVariableCondition.newBuilder()
                        .setWorkflowVariableName(variableName)
                        .setExpectedValue(Value.newBuilder().setBoolValue(true).build())
                        .setComparisonOperator(com.Orka.apiContract.generated.ComparisonOperator.EQUAL)
                        .build())
                .build();
    }

    public static com.Orka.apiContract.generated.InputBinding inputBinding(String destinationPath, DataReference source) {
        return com.Orka.apiContract.generated.InputBinding.newBuilder()
                .setDestinationPath(destinationPath)
                .setDataReference(source)
                .build();
    }

    public static com.Orka.apiContract.generated.ScriptDefinition minimalScriptDefinition() {
        return com.Orka.apiContract.generated.ScriptDefinition.newBuilder()
                .setScriptName("test-script")
                .setDockerImage("alpine:latest")
                .setVersion(1)
                .setEntryCommand("echo hello")
                .setTimeout(30)
                .addEnvironmentVariables(com.Orka.apiContract.generated.VariableDefinition.newBuilder()
                        .setName("myVar")
                        .setVariableType(com.Orka.apiContract.generated.VariableType.STRING)
                        .setDefaultValue(Value.newBuilder().setStringValue("env").build())
                        .build())
                .build();
    }

    public static VariableDefinition internalVariable(UUID workflowId, String name) throws Exception {
        return VariableDefinition.builder()
                .workflowDefinitionId(workflowId)
                .name(name)
                .type(VariableType.STRING)
                .defaultValue(OBJECT_MAPPER.readTree("\"default\""))
                .build();
    }

    public static WorkflowDefinition workflowDefinitionForResolver() throws Exception {
        UUID workflowId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        VariableDefinition variable = VariableDefinition.builder()
                .id(UUID.randomUUID())
                .workflowDefinitionId(workflowId)
                .name("flag")
                .type(VariableType.BOOLEAN)
                .defaultValue(OBJECT_MAPPER.readTree("true"))
                .build();

        StateInputReference inputRef = StateInputReference.builder()
                .taskDefinitionName("task1")
                .stateDefinitionName("state1")
                .jsonPath("$.x")
                .build();

        StateOutputReference outputRef = StateOutputReference.builder()
                .taskDefinitionName("task1")
                .stateDefinitionName("state1")
                .jsonPath("$.y")
                .build();

        WorkflowVariableCondition variableCondition = WorkflowVariableCondition.builder()
                .name("varCond")
                .workflowDefinitionId(workflowId)
                .variableName("flag")
                .operator(com.Orka.ENUM.typeEnums.ComparisonOperator.EQUAL)
                .expectedValue(OBJECT_MAPPER.readTree("true"))
                .build();

        StateInputCondition inputCondition = StateInputCondition.builder()
                .name("inputCond")
                .workflowDefinitionId(workflowId)
                .reference(inputRef)
                .operator(com.Orka.ENUM.typeEnums.ComparisonOperator.EQUAL)
                .expectedValue(OBJECT_MAPPER.readTree("\"ok\""))
                .build();

        Condition condition = Condition.builder()
                .name("root")
                .expression("A && B")
                .atomicConditions(List.of(variableCondition, inputCondition))
                .build();
        variableCondition.setCondition(condition);
        inputCondition.setCondition(condition);

        InputBinding binding = InputBinding.builder()
                .destinationJsonPath("$.target")
                .source(outputRef)
                .build();

        InputDefinition inputDefinition = InputDefinition.builder()
                .jsonSchema("{}")
                .bindings(List.of(binding))
                .build();
        binding.setInputDefinition(inputDefinition);

        StateDefinition state = StateDefinition.builder()
                .id(UUID.randomUUID())
                .name("state1")
                .priority(1)
                .internalState(ORKA_INTERNAL_STATE.RUNNING)
                .conditionToBecomeActive(condition)
                .inputDefinition(inputDefinition)
                .build();

        TaskDefinition task = TaskDefinition.builder()
                .id(taskId)
                .name("task1")
                .description("desc")
                .workflowDefinitionId(workflowId)
                .states(List.of(state))
                .authorizations(List.of())
                .build();
        state.setTaskDefinition(task);

        return WorkflowDefinition.builder()
                .id(workflowId)
                .name("resolver-wf")
                .description("desc")
                .version(1)
                .creatorName("creator")
                .startTaskDefinitionId(taskId)
                .startState(state)
                .tasks(List.of(task))
                .variableDefinitions(List.of(variable))
                .authorizationList(List.of())
                .build();
    }

    public static WorkflowDefinition persistedWorkflowDefinition() {
        UUID workflowId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        TaskDefinition task = TaskDefinition.builder()
                .id(taskId)
                .name("task1")
                .description("desc")
                .workflowDefinitionId(workflowId)
                .build();

        StateDefinition state = StateDefinition.builder()
                .id(stateId)
                .name("startState")
                .priority(1)
                .taskDefinition(task)
                .build();

        WorkflowDefinitionAuthorization auth = WorkflowDefinitionAuthorization.builder()
                .username("viewer")
                .workflowDefinitionId(workflowId)
                .authRole(WORKFLOW_DEFINITION_AUTH_ROLE.VIEWER)
                .build();

        return WorkflowDefinition.builder()
                .id(workflowId)
                .name("Persisted Workflow")
                .creatorName("creator")
                .startTaskDefinitionId(taskId)
                .startState(state)
                .authorizationList(List.of(auth))
                .tasks(List.of(task))
                .variableDefinitions(List.of())
                .build();
    }
}
