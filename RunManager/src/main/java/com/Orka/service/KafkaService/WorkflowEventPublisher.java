package com.Orka.service.KafkaService;

import com.Orka.apiContract.generated.WorkflowRunCreatedEvent;
import com.Orka.events.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class WorkflowEventPublisher {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    public WorkflowEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean publish(UUID workflowRunId){
        log.info("Publishing workflow run id {}", workflowRunId);
        if(workflowRunId == null){
            throw new IllegalArgumentException("Workflow run id cannot be null");
        }
        WorkflowRunCreatedEvent workflowRunCreatedEvent = WorkflowRunCreatedEvent.newBuilder().setWorkflowRunId(workflowRunId.toString()).build();
        try{
            kafkaTemplate.send(
                    KafkaTopics.WORKFLOW_CREATED, // topic
                    workflowRunId.toString(), //  key (index to partition)
                    workflowRunCreatedEvent.toByteArray()).get(); // data to be sent on wire
            log.info("Workflow run published successfully for {}", workflowRunId);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            log.error("Error publishing workflow run id {} at line number 33 ", workflowRunId, e);
            return false;
        }
    }
}
