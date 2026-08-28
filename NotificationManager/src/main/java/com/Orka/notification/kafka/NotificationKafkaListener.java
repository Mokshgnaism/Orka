package com.Orka.notification.kafka;

import com.Orka.apiContract.generated.ProvideInputRequest;
import com.Orka.apiContract.generated.ScriptExecutionResult;
import com.Orka.apiContract.generated.StateRunStartedEvent;
import com.Orka.apiContract.generated.WorkflowDefinitionCreatedEvent;
import com.Orka.apiContract.generated.WorkflowRunCreatedEvent;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.events.KafkaTopics;
import com.Orka.notification.model.NotificationScope;
import com.Orka.notification.model.NotificationSeverity;
import com.Orka.notification.service.NotificationDispatchService;
import com.Orka.repository.StateRunRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.repository.WorkflowRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class NotificationKafkaListener {
    private final NotificationDispatchService dispatchService;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final StateRunRepository stateRunRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final ObjectMapper objectMapper;

    public NotificationKafkaListener(
            NotificationDispatchService dispatchService,
            WorkflowRunRepository workflowRunRepository,
            TaskRunRepository taskRunRepository,
            StateRunRepository stateRunRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            ObjectMapper objectMapper) {
        this.dispatchService = dispatchService;
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.stateRunRepository = stateRunRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    @KafkaListener(
            topics = KafkaTopics.WORKFLOW_DEFINITION_CREATED,
            groupId = KafkaTopics.WORKFLOW_DEFINITION_CREATED + "-notification-manager-group"
    )
    public void listenWorkflowDefinitionCreated(byte[] event) {
        try {
            WorkflowDefinitionCreatedEvent createdEvent = WorkflowDefinitionCreatedEvent.parseFrom(event);
            UUID workflowDefinitionId = UUID.fromString(createdEvent.getWorkflowDefinitionId());
            WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowDefinitionId).orElse(null);

            dispatchService.dispatchSystemNotification(
                    "kafka.workflow-definition-created",
                    NotificationScope.WORKFLOW_DEFINITION,
                    workflowDefinitionId,
                    "Workflow definition created",
                    workflowDefinition == null
                            ? "Workflow definition was created"
                            : "Workflow definition " + workflowDefinition.getName() + " was created",
                    NotificationSeverity.SUCCESS,
                    workflowDefinitionPayload(workflowDefinitionId, workflowDefinition)
            );
        } catch (InvalidProtocolBufferException | IllegalArgumentException exception) {
            log.warn("Unable to parse workflow definition created event", exception);
        }
    }

    @Transactional(readOnly = true)
    @KafkaListener(
            topics = KafkaTopics.WORKFLOW_CREATED,
            groupId = KafkaTopics.WORKFLOW_CREATED + "-notification-manager-group"
    )
    public void listenWorkflowCreated(byte[] event) {
        try {
            WorkflowRunCreatedEvent createdEvent = WorkflowRunCreatedEvent.parseFrom(event);
            UUID workflowRunId = UUID.fromString(createdEvent.getWorkflowRunId());
            WorkflowRun workflowRun = workflowRunRepository.findById(workflowRunId).orElse(null);

            dispatchService.dispatchSystemNotification(
                    "kafka.workflow-created",
                    NotificationScope.WORKFLOW_RUN,
                    workflowRunId,
                    "Workflow run created",
                    workflowRun == null
                            ? "Workflow run was created"
                            : "Workflow run " + workflowRun.getId() + " was created",
                    NotificationSeverity.SUCCESS,
                    workflowRunPayload(workflowRunId, workflowRun)
            );
        } catch (InvalidProtocolBufferException | IllegalArgumentException exception) {
            log.warn("Unable to parse workflow created event", exception);
        }
    }

    @Transactional(readOnly = true)
    @KafkaListener(
            topics = KafkaTopics.STATE_ACTIVATED,
            groupId = KafkaTopics.STATE_ACTIVATED + "-notification-manager-group"
    )
    public void listenStateActivated(byte[] event) {
        try {
            StateRunStartedEvent stateRunStartedEvent = StateRunStartedEvent.parseFrom(event);
            UUID stateRunId = UUID.fromString(stateRunStartedEvent.getId());
            StateRun stateRun = stateRunRepository.findById(stateRunId).orElse(null);

            dispatchService.dispatchSystemNotification(
                    "kafka.state-activated",
                    NotificationScope.STATE_RUN,
                    stateRunId,
                    "State activated",
                    stateRun == null
                            ? "A workflow state was activated"
                            : "State " + safeStateName(stateRun) + " was activated",
                    NotificationSeverity.INFO,
                    stateRunPayload(stateRunId, stateRun)
            );
        } catch (InvalidProtocolBufferException | IllegalArgumentException exception) {
            log.warn("Unable to parse state activated event", exception);
        }
    }

    @Transactional(readOnly = true)
    @KafkaListener(
            topics = KafkaTopics.SCRIPT_COMPLETED,
            groupId = KafkaTopics.SCRIPT_COMPLETED + "-notification-manager-group"
    )
    public void listenScriptCompleted(byte[] event) {
        try {
            ScriptExecutionResult result = ScriptExecutionResult.parseFrom(event);
            UUID stateRunId = UUID.fromString(result.getStateRunId());
            StateRun stateRun = stateRunRepository.findById(stateRunId).orElse(null);

            dispatchService.dispatchSystemNotification(
                    "kafka.script-completed",
                    NotificationScope.STATE_RUN,
                    stateRunId,
                    result.getExitCode() == 0 ? "Script completed" : "Script failed",
                    result.getError() == null || result.getError().isBlank()
                            ? "Script execution finished"
                            : result.getError(),
                    result.getExitCode() == 0 ? NotificationSeverity.SUCCESS : NotificationSeverity.ERROR,
                    scriptResultPayload(stateRunId, stateRun, result)
            );
        } catch (InvalidProtocolBufferException | IllegalArgumentException exception) {
            log.warn("Unable to parse script completed event", exception);
        }
    }

    @Transactional(readOnly = true)
    @KafkaListener(
            topics = KafkaTopics.TASK_INPUT_PROVIDED,
            groupId = KafkaTopics.TASK_INPUT_PROVIDED + "-notification-manager-group"
    )
    public void listenTaskInputProvided(byte[] event) {
        try {
            ProvideInputRequest request = ProvideInputRequest.parseFrom(event);
            UUID taskRunId = UUID.fromString(request.getTaskRunId());
            TaskRun taskRun = taskRunRepository.findById(taskRunId).orElse(null);

            dispatchService.dispatchSystemNotification(
                    "kafka.task-input-provided",
                    NotificationScope.TASK_RUN,
                    taskRunId,
                    "Task input provided",
                    "Input was provided for " + (taskRun == null ? "a task" : taskRun.getTaskDefinitionName()),
                    NotificationSeverity.INFO,
                    taskInputPayload(taskRunId, taskRun, request)
            );
        } catch (InvalidProtocolBufferException | IllegalArgumentException exception) {
            log.warn("Unable to parse task input provided event", exception);
        }
    }

    private ObjectNode workflowDefinitionPayload(UUID workflowDefinitionId, WorkflowDefinition workflowDefinition) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("workflowDefinitionId", workflowDefinitionId.toString());
        if (workflowDefinition != null) {
            payload.put("name", workflowDefinition.getName());
            payload.put("version", workflowDefinition.getVersion());
            payload.put("creatorName", workflowDefinition.getCreatorName());
        }
        return payload;
    }

    private ObjectNode workflowRunPayload(UUID workflowRunId, WorkflowRun workflowRun) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("workflowRunId", workflowRunId.toString());
        if (workflowRun != null) {
            payload.put("status", workflowRun.getStatus() == null ? null : workflowRun.getStatus().name());
            payload.put("startedAt", workflowRun.getStartedAt() == null ? null : workflowRun.getStartedAt().toString());
            if (workflowRun.getWorkflowDefinition() != null) {
                payload.put("workflowDefinitionId", workflowRun.getWorkflowDefinition().getId().toString());
                payload.put("workflowDefinitionName", workflowRun.getWorkflowDefinition().getName());
            }
        }
        return payload;
    }

    private ObjectNode stateRunPayload(UUID stateRunId, StateRun stateRun) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("stateRunId", stateRunId.toString());
        if (stateRun != null) {
            payload.put("status", stateRun.getStatus() == null ? null : stateRun.getStatus().name());
            payload.put("stateDefinitionName", safeStateName(stateRun));
            payload.put("enteredAt", stateRun.getEnteredAt() == null ? null : stateRun.getEnteredAt().toString());
            putTaskRunDetails(payload, stateRun.getTaskRun());
        }
        return payload;
    }

    private ObjectNode scriptResultPayload(UUID stateRunId, StateRun stateRun, ScriptExecutionResult result) {
        ObjectNode payload = stateRunPayload(stateRunId, stateRun);
        payload.put("exitCode", result.getExitCode());
        payload.put("error", result.getError());
        payload.put("stdout", result.getStdout());
        payload.put("stderr", result.getStderr());
        if (result.hasOutput()) {
            try {
                payload.put("output", JsonFormat.printer().print(result.getOutput()));
            } catch (InvalidProtocolBufferException exception) {
                payload.put("output", result.getOutput().toString());
            }
        }
        return payload;
    }

    private ObjectNode taskInputPayload(UUID taskRunId, TaskRun taskRun, ProvideInputRequest request) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("taskRunId", taskRunId.toString());
        payload.put("path", request.getPath());
        payload.put("username", request.getUsername());
        if (request.hasInputVal()) {
            try {
                payload.put("inputVal", JsonFormat.printer().print(request.getInputVal()));
            } catch (InvalidProtocolBufferException exception) {
                payload.put("inputVal", request.getInputVal().toString());
            }
        }
        putTaskRunDetails(payload, taskRun);
        return payload;
    }

    private void putTaskRunDetails(ObjectNode payload, TaskRun taskRun) {
        if (taskRun == null) {
            return;
        }

        payload.put("taskRunId", taskRun.getId().toString());
        payload.put("taskDefinitionName", taskRun.getTaskDefinitionName());
        payload.put("currentStateDefinitionName", taskRun.getCurrentStateDefinitionName());
        payload.put("status", taskRun.getStatus() == null ? null : taskRun.getStatus().name());
        if (taskRun.getWorkflowRun() != null) {
            payload.put("workflowRunId", taskRun.getWorkflowRun().getId().toString());
        }
        if (taskRun.getTaskDefinition() != null) {
            payload.put("taskDefinitionId", taskRun.getTaskDefinition().getId().toString());
        }
    }

    private String safeStateName(StateRun stateRun) {
        if (stateRun.getStateDefinition() == null) {
            return "unknown";
        }
        return stateRun.getStateDefinition().getName();
    }
}
