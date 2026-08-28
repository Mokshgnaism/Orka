package com.Orka.notification.service;

import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.authorization.TaskRunAuthorization;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.authorization.WorkflowRunAuthorization;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.notification.model.NotificationScope;
import com.Orka.repository.StateRunRepository;
import com.Orka.repository.TaskDefinitionRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.repository.UserRepository;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.repository.WorkflowRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class RecipientResolver {
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunRepository taskRunRepository;
    private final StateRunRepository stateRunRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final UserRepository userRepository;

    public RecipientResolver(
            WorkflowRunRepository workflowRunRepository,
            TaskRunRepository taskRunRepository,
            StateRunRepository stateRunRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            TaskDefinitionRepository taskDefinitionRepository,
            UserRepository userRepository) {
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunRepository = taskRunRepository;
        this.stateRunRepository = stateRunRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Set<String> resolve(NotificationScope scope, UUID resourceId, String recipientUsername) {
        return switch (scope) {
            case WORKFLOW_RUN -> workflowRunRepository.findById(resourceId)
                    .map(this::workflowRunRecipients)
                    .orElseGet(Set::of);
            case TASK_RUN -> taskRunRepository.findById(resourceId)
                    .map(this::taskRunRecipients)
                    .orElseGet(Set::of);
            case STATE_RUN -> stateRunRepository.findById(resourceId)
                    .map(this::stateRunRecipients)
                    .orElseGet(Set::of);
            case WORKFLOW_DEFINITION -> workflowDefinitionRepository.findById(resourceId)
                    .map(this::workflowDefinitionRecipients)
                    .orElseGet(Set::of);
            case TASK_DEFINITION -> taskDefinitionRepository.findById(resourceId)
                    .map(this::taskDefinitionRecipients)
                    .orElseGet(Set::of);
            case USER -> resolveSingleUser(recipientUsername);
        };
    }

    @Transactional(readOnly = true)
    public boolean userCanPublish(String username, NotificationScope scope, UUID resourceId, String recipientUsername) {
        if (username == null || username.isBlank()) {
            return false;
        }

        if (scope == NotificationScope.USER) {
            return username.equals(recipientUsername);
        }

        return resolve(scope, resourceId, recipientUsername).contains(username);
    }

    private Set<String> workflowRunRecipients(WorkflowRun workflowRun) {
        Set<String> recipients = new HashSet<>();
        addWorkflowRunAuth(recipients, workflowRun);
        addWorkflowDefinitionAuth(recipients, workflowRun.getWorkflowDefinition());
        addWorkflowCreator(recipients, workflowRun.getWorkflowDefinition());
        addTaskRunAuth(recipients, workflowRun.getTaskRuns());
        addTaskDefinitionAuthFromTaskRuns(recipients, workflowRun.getTaskRuns());
        return recipients;
    }

    private Set<String> taskRunRecipients(TaskRun taskRun) {
        Set<String> recipients = new HashSet<>();
        addTaskRunAuth(recipients, List.of(taskRun));
        addTaskDefinitionAuth(recipients, taskRun.getTaskDefinition());
        if (taskRun.getWorkflowRun() != null) {
            addWorkflowRunAuth(recipients, taskRun.getWorkflowRun());
            addWorkflowDefinitionAuth(recipients, taskRun.getWorkflowRun().getWorkflowDefinition());
            addWorkflowCreator(recipients, taskRun.getWorkflowRun().getWorkflowDefinition());
        }
        return recipients;
    }

    private Set<String> stateRunRecipients(StateRun stateRun) {
        if (stateRun.getTaskRun() == null) {
            return Set.of();
        }
        return taskRunRecipients(stateRun.getTaskRun());
    }

    private Set<String> workflowDefinitionRecipients(WorkflowDefinition workflowDefinition) {
        Set<String> recipients = new HashSet<>();
        addWorkflowDefinitionAuth(recipients, workflowDefinition);
        addWorkflowCreator(recipients, workflowDefinition);
        if (workflowDefinition.getTasks() != null) {
            workflowDefinition.getTasks().forEach(taskDefinition -> addTaskDefinitionAuth(recipients, taskDefinition));
        }
        return recipients;
    }

    private Set<String> taskDefinitionRecipients(TaskDefinition taskDefinition) {
        Set<String> recipients = new HashSet<>();
        addTaskDefinitionAuth(recipients, taskDefinition);
        addWorkflowDefinitionAuth(recipients, taskDefinition.getWorkflowDefinition());
        addWorkflowCreator(recipients, taskDefinition.getWorkflowDefinition());
        return recipients;
    }

    private Set<String> resolveSingleUser(String recipientUsername) {
        if (recipientUsername == null || recipientUsername.isBlank()) {
            return Set.of();
        }
        return userRepository.findByUsername(recipientUsername).isPresent()
                ? Set.of(recipientUsername)
                : Set.of();
    }

    private void addWorkflowRunAuth(Set<String> recipients, WorkflowRun workflowRun) {
        if (workflowRun == null || workflowRun.getAuthorizations() == null) {
            return;
        }
        workflowRun.getAuthorizations().stream()
                .map(WorkflowRunAuthorization::getUsername)
                .forEach(username -> addUsername(recipients, username));
    }

    private void addTaskRunAuth(Set<String> recipients, Collection<TaskRun> taskRuns) {
        if (taskRuns == null) {
            return;
        }
        for (TaskRun taskRun : taskRuns) {
            if (taskRun == null || taskRun.getAuthorizations() == null) {
                continue;
            }
            taskRun.getAuthorizations().stream()
                    .map(TaskRunAuthorization::getUsername)
                    .forEach(username -> addUsername(recipients, username));
        }
    }

    private void addWorkflowDefinitionAuth(Set<String> recipients, WorkflowDefinition workflowDefinition) {
        if (workflowDefinition == null || workflowDefinition.getAuthorizationList() == null) {
            return;
        }
        workflowDefinition.getAuthorizationList().stream()
                .map(WorkflowDefinitionAuthorization::getUsername)
                .forEach(username -> addUsername(recipients, username));
    }

    private void addWorkflowCreator(Set<String> recipients, WorkflowDefinition workflowDefinition) {
        if (workflowDefinition != null) {
            addUsername(recipients, workflowDefinition.getCreatorName());
        }
    }

    private void addTaskDefinitionAuthFromTaskRuns(Set<String> recipients, Collection<TaskRun> taskRuns) {
        if (taskRuns == null) {
            return;
        }
        taskRuns.stream()
                .map(TaskRun::getTaskDefinition)
                .forEach(taskDefinition -> addTaskDefinitionAuth(recipients, taskDefinition));
    }

    private void addTaskDefinitionAuth(Set<String> recipients, TaskDefinition taskDefinition) {
        if (taskDefinition == null || taskDefinition.getAuthorizations() == null) {
            return;
        }
        taskDefinition.getAuthorizations().stream()
                .map(TaskDefinitionAuthorization::getUsername)
                .forEach(username -> addUsername(recipients, username));
    }

    private void addUsername(Set<String> recipients, String username) {
        if (username != null && !username.isBlank()) {
            recipients.add(username);
        }
    }
}
