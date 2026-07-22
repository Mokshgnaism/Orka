package com.Orka.controller;

import com.Orka.apiContract.generated.ScriptExecutionResult;
import com.Orka.apiContract.generated.WorkflowRunCreatedEvent;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.repository.StateRunRepository;
import com.Orka.service.TaskRunEngine;
import com.Orka.service.WorkflowRunEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.Orka.events.KafkaTopics;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class KafkaController {
    private final StateRunRepository stateRunRepository;
    private final TaskRunEngine taskRunEngine;
    private final WorkflowRunEngine workflowRunEngine;

    public KafkaController(StateRunRepository stateRunRepository, TaskRunEngine taskRunEngine, WorkflowRunEngine workflowRunEngine) {
        this.stateRunRepository = stateRunRepository;
        this.taskRunEngine = taskRunEngine;
        this.workflowRunEngine = workflowRunEngine;
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
            EvaluationContext evaluationContext = new EvaluationContext(workflowRun,workflowDefinition);
            taskRunEngine.update(stateRun.getTaskRun(),workflowRun,evaluationContext);
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

    public void listen_input_provided(byte[] event) {

    }

}
