package com.Orka.controller;

import com.Orka.apiContract.generated.ProvideInputRequest;
import com.Orka.apiContract.generated.ScriptExecutionResult;
import com.Orka.apiContract.generated.WorkflowRunCreatedEvent;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.repository.StateRunRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.service.TaskRunEngine;
import com.Orka.service.WorkflowRunEngine;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.Orka.events.KafkaTopics;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class KafkaController {
    private final StateRunRepository stateRunRepository;
    private final TaskRunEngine taskRunEngine;
    private final WorkflowRunEngine workflowRunEngine;
    private final TaskRunRepository taskRunRepository;

    public KafkaController(StateRunRepository stateRunRepository, TaskRunEngine taskRunEngine, WorkflowRunEngine workflowRunEngine, TaskRunRepository taskRunRepository) {
        this.stateRunRepository = stateRunRepository;
        this.taskRunEngine = taskRunEngine;
        this.workflowRunEngine = workflowRunEngine;
        this.taskRunRepository = taskRunRepository;
        System.out.println("Kafka Controller initialized");
    }

    @KafkaListener(
            topics = KafkaTopics.WORKFLOW_CREATED,
            groupId = KafkaTopics.WORKFLOW_CREATED + "- automata service -group"
    )
    public void listen_workflow_created(byte[] event) {
        WorkflowRunCreatedEvent workflowRunCreatedEvent = null;
        System.out.println("Received Workflow Created Event from topic - " + event.length);
        try {
            workflowRunCreatedEvent = WorkflowRunCreatedEvent.parseFrom(event);

            workflowRunEngine.advanceWorkflow(UUID.fromString(workflowRunCreatedEvent.getWorkflowRunId()), true);
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            log.error("invalidProtocolBufferException", invalidProtocolBufferException);
            log.error("error at workflow listen_workflow_created");
            invalidProtocolBufferException.printStackTrace();
        } catch (Exception e) {
            log.error("error at workflow listen_workflow_created", e);
            e.printStackTrace();
            throw e;
        }
    }

    @KafkaListener(
            topics = KafkaTopics.SCRIPT_COMPLETED,
            groupId = KafkaTopics.SCRIPT_COMPLETED + "-automata service -group"
    )
    public void listen_script_completed(byte[] event) {
        try{
            ScriptExecutionResult scriptExecutionResult = ScriptExecutionResult.parseFrom(event);
            log.info("Received ScriptExecutionResult from Kafka topic: {}", scriptExecutionResult);
            StateRun stateRun = stateRunRepository.findById(UUID.fromString(scriptExecutionResult.getStateRunId())).orElse(null);
            if(stateRun == null){
                throw new RuntimeException("state run not found");
            }
            WorkflowRun workflowRun = stateRun.getTaskRun().getWorkflowRun();
            WorkflowDefinition workflowDefinition = workflowRun.getWorkflowDefinition();
            taskRunEngine.update(stateRun.getTaskRun());
            workflowRunEngine.advanceWorkflow(workflowRun.getId(),false);
        }catch (InvalidProtocolBufferException invalidProtocolBufferException){
            log.error("invalidProtocolBufferException",invalidProtocolBufferException);
            log.error("error at listen_script_completed");
            invalidProtocolBufferException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.TASK_INPUT_PROVIDED,
            groupId = KafkaTopics.TASK_INPUT_PROVIDED + "-automata -group"
    )
    public void listen_input_provided(byte[] event) throws InvalidProtocolBufferException {
        ProvideInputRequest request = ProvideInputRequest.parseFrom(event);
        log.info("[HUMAN] Recieved input provided event for {}",request.getTaskRunId());
        TaskRun taskRun = taskRunRepository.findById(UUID.fromString(request.getTaskRunId())).orElse(null);
        if(taskRun==null)
            return;
        taskRunEngine.update(taskRun);
        workflowRunEngine.advanceWorkflow(taskRun.getWorkflowRun().getId(),false);
    }

}
