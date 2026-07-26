package com.Orka.events;

public final class KafkaTopics {

    public static final String WORKFLOW_CREATED = "workflow-created";

    public static final String STATE_ACTIVATED = "state-activated";

    public static final String TASK_COMPLETED = "task-completed";

    public static final String SCRIPT_COMPLETED = "script-completed";

    public static final String TASK_INPUT_PROVIDED = "task-input-provided";

    public static final String WORKFLOW_DEFINITION_CREATED = "workflow-definition-created";

    private KafkaTopics() {}
}