package com.Orka.service;

import com.Orka.Store.Store;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.authorization.TaskRunAuthorization;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.authorization.WorkflowRunAuthorization;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.repository.TaskDefinitionAuthorizationRepository;
import com.Orka.repository.TaskRunAuthorizationRepository;
import com.Orka.repository.WorkflowDefinitionAuthorizationRepository;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.repository.WorkflowRunAuthorizationRepository;
import com.Orka.repository.WorkflowRunRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AuthAdditionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskDefinitionAuthorizationRepository taskDefinitionAuthorizationRepository;
    private final WorkflowDefinitionAuthorizationRepository workflowDefinitionAuthorizationRepository;
    private final TaskRunAuthorizationRepository taskRunAuthorizationRepository;
    private final WorkflowRunAuthorizationRepository workflowRunAuthorizationRepository;

    public AuthAdditionService(
            WorkflowDefinitionRepository workflowDefinitionRepository,
            WorkflowRunRepository workflowRunRepository,
            TaskDefinitionAuthorizationRepository taskDefinitionAuthorizationRepository,
            WorkflowDefinitionAuthorizationRepository workflowDefinitionAuthorizationRepository,
            TaskRunAuthorizationRepository taskRunAuthorizationRepository,
            WorkflowRunAuthorizationRepository workflowRunAuthorizationRepository) {

        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.taskDefinitionAuthorizationRepository = taskDefinitionAuthorizationRepository;
        this.workflowDefinitionAuthorizationRepository = workflowDefinitionAuthorizationRepository;
        this.taskRunAuthorizationRepository = taskRunAuthorizationRepository;
        this.workflowRunAuthorizationRepository = workflowRunAuthorizationRepository;
    }

    public void addDefinitionAuthToMemory(UUID workflowDefinitionId) {

        WorkflowDefinition workflowDefinition =
                workflowDefinitionRepository.findById(workflowDefinitionId).orElse(null);

        if (workflowDefinition == null) {
            return;
        }

        Store.LOCK.writeLock().lock();

        try {

            for (WorkflowDefinitionAuthorization auth : workflowDefinition.getAuthorizationList()) {
                addAuthorization(
                        Store.workflowDefinitionAuthorizations,
                        auth.getUsername(),
                        auth
                );
            }

            for (TaskDefinition taskDefinition : workflowDefinition.getTasks()) {

                for (TaskDefinitionAuthorization auth : taskDefinition.getAuthorizations()) {
                    addAuthorization(
                            Store.taskDefinitionAuthorizations,
                            auth.getUsername(),
                            auth
                    );
                }
            }

        } finally {
            Store.LOCK.writeLock().unlock();
        }
    }

    public void addRunAuthToMemory(UUID workflowRunId) {

        WorkflowRun workflowRun =
                workflowRunRepository.findById(workflowRunId).orElse(null);

        if (workflowRun == null) {
            return;
        }

        Store.LOCK.writeLock().lock();

        try {

            for (WorkflowRunAuthorization auth : workflowRun.getAuthorizations()) {
                addAuthorization(
                        Store.workflowRunAuthorizations,
                        auth.getUsername(),
                        auth
                );
            }

            workflowRun.getTaskRuns().forEach(taskRun -> {

                for (TaskRunAuthorization auth : taskRun.getAuthorizations()) {
                    addAuthorization(
                            Store.taskRunAuthorizations,
                            auth.getUsername(),
                            auth
                    );
                }

            });

        } finally {
            Store.LOCK.writeLock().unlock();
        }
    }

    public void load() {

        Store.LOCK.writeLock().lock();

        try {

            workflowDefinitionAuthorizationRepository.findAll().forEach(auth ->
                    addAuthorization(
                            Store.workflowDefinitionAuthorizations,
                            auth.getUsername(),
                            auth));

            taskDefinitionAuthorizationRepository.findAll().forEach(auth ->
                    addAuthorization(
                            Store.taskDefinitionAuthorizations,
                            auth.getUsername(),
                            auth));

            workflowRunAuthorizationRepository.findAll().forEach(auth ->
                    addAuthorization(
                            Store.workflowRunAuthorizations,
                            auth.getUsername(),
                            auth));

            taskRunAuthorizationRepository.findAll().forEach(auth ->
                    addAuthorization(
                            Store.taskRunAuthorizations,
                            auth.getUsername(),
                            auth));

        } finally {
            Store.LOCK.writeLock().unlock();
        }
    }

    private <T> void addAuthorization(
            Map<String, Set<T>> map,
            String username,
            T authorization) {

        map.computeIfAbsent(
                username,
                k -> ConcurrentHashMap.newKeySet()
        ).add(authorization);
    }
}