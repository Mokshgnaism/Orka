package com.Orka.controller;

import com.Orka.apiContract.generated.WorkflowDefinitionCreatedEvent;
import com.Orka.events.KafkaTopics;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
public class KafkaController {

    @KafkaListener(
            topics = KafkaTopics.WORKFLOW_DEFINITION_CREATED,
            groupId = KafkaTopics.WORKFLOW_DEFINITION_CREATED + "-authoirzation service -groupd"
    )
    public void addAuthorizations(byte[]event){
        try {
            WorkflowDefinitionCreatedEvent workflowDefinitionCreatedEvent = WorkflowDefinitionCreatedEvent.parseFrom(event);
            UUID workflowDefinitionId = UUID.fromString(workflowDefinitionCreatedEvent.getWorkflowDefinitionId());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
