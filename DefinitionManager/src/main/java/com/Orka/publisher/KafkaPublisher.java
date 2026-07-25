package com.Orka.publisher;

import com.Orka.apiContract.generated.WorkflowDefinitionCreatedEvent;
import com.Orka.events.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaPublisher {
    private final KafkaTemplate<String , byte[]> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish_workflowDefinitionStarted(UUID id){
        kafkaTemplate.send(
                KafkaTopics.WORKFLOW_DEFINITION_CREATED,
                WorkflowDefinitionCreatedEvent.newBuilder().setWorkflowDefinitionId(id.toString()).build().toByteArray()
        );
    }
}
